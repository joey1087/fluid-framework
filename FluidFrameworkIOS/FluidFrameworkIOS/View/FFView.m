//
//  FFView.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 26/02/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFView.h"
#import "View.h"
#include "java/lang/Double.h"
#include "java/lang/Boolean.h"
#include "Screen.h"
#include "ViewBehavior.h"
#include "ViewBehaviorLabel.h"
#include "ViewBehaviorButton.h"
#include "ViewBehaviorTable.h"
#include "ViewBehaviorImage.h"
#include "ViewBehaviorSubview.h"
#include "ViewBehaviorWebView.h"
#include "Color.h"
#include "MenuButtonItem.h"
#include "FFTableViewDelegate.h"
#include "Constraints.h"
#include "java/util/HashMap.h"
#include "java/util/HashSet.h"
#include "Layout.h"
#include "FFFluidAppDelegate.h"
#import "FluidApp.h"
#include "ImageManager.h"
#include "FFDataNotificationService.h"
#include "FFWebView.h"
#include "ViewBehaviorTextfield.h"
#include "FFTextField.h"
#include "FluidViewFactory.h"
#include "FFViewFactoryRegistration.h"
#include "GlobalState.h"
#include "EventsManager.h"
#include "ActionListener.h"
#import "ViewPosition.h"
#import "DataModelManager.h"
#import "java/lang/Integer.h"
#import "FFTableView.h"
#import "UIService.h"

static const CGFloat KEYBOARD_ANIMATION_DURATION = 0.3;
static const CGFloat PORTRAIT_KEYBOARD_HEIGHT = 216;
static const CGFloat LANDSCAPE_KEYBOARD_HEIGHT = 162;
static const CGFloat KEYBOARD_PADDING_TOP = 10;

static int containerId = 1;

@interface FFView ()

@property (nonatomic, assign) CGFloat animatedDistance;
@property (nonatomic, assign) CGFloat keyboardHeight;
@property (nonatomic, assign) UIView *currentTextField;
@property (nonatomic, assign) BOOL dismissCurrentKeyboardWithTap;
@property (nonatomic, strong) NSMutableDictionary *tappedOutsideWhileFocusedListeners;
@property (nonatomic, assign) BOOL hasAddedConditionalListener;
@property (nonatomic, assign) int containerId;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, assign) BOOL eligableForTap;
@property (nonatomic, assign) BOOL keyboardUp;
@property (nonatomic, assign) float scrollViewContentSize;
@property (nonatomic, assign) BOOL hasRanCleanup;

@end

@implementation FFView

- (id)initWithFrame:(CGRect)frame {
    [self doesNotRecognizeSelector:_cmd];
    return nil;
}

- (id)initWithFrame:(CGRect)frame viewPath:(NSString *)viewPath layout:(FFTLayout *)layout rootFFView:(FFView *)rootFFView inTableView:(UITableView *)tableView {
    
    if (self = [super initWithFrame:frame]) {
        self.layout = layout;
        self.viewsById = [NSMutableDictionary dictionary];
        self.tappedOutsideWhileFocusedListeners = [NSMutableDictionary dictionary];
        self.rootFFView = rootFFView;
        self.listenToDataModelChanges = YES;
        self.dataModelListenerId = layout->id__;
        self.viewPath = viewPath;
        self.hasAddedConditionalListener = NO;
        self.tableView = tableView;
        self.containerId = containerId++;
        
        if (layout->backgroundColor_) {
            self.backgroundColor = [FFView color:layout->backgroundColor_];
        }
    }
    return self;
}

- (void) dealloc {
    
    [self cleanup];
}

#pragma mark FFTDataChangeListener

- (void)dataChangedWithNSString:(NSString *)key
              withNSStringArray:(IOSObjectArray *)subKeys {
    
    if (self.hasRanCleanup) {
        
        // hstdbc This is a safeguard to a memory leak. This is a temporary band aid. The memory leak should be found. (subview repeat inside a table)
        return;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_sync(dispatch_get_main_queue(), ^{
           //[self createOrUpdateViews:[self bounds]];
            if (self.hasRanCleanup) {
                // hstdbc This is a safeguard to a memory leak. This is a temporary band aid. The memory leak should be found. (subview repeat inside a table)
                return;
            }
            [self setScrollViewContentSize];
            [self createOrUpdateViews:self.frame fromDataListener:YES];
            [self.tableView reloadData];
        });
    });
}

- (void)dataRemovedWithNSString:(NSString *)key {
    
}

#pragma mark end FFTDataChangeListener

- (void)layoutSubviews {
    
    [super layoutSubviews];
    
    [self createOrLayoutViews];
}

- (void)createOrLayoutViews {
    
    if ([self.layout isWrapInScrollView] && self.scrollView == nil) {
        
        self.scrollView = [[UIScrollView alloc] initWithFrame:self.frame];
        
        [self setScrollViewContentSize];
        
        UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleTapFromScrollGestureRecognizer:)];
        singleTap.cancelsTouchesInView = NO;
        [self.scrollView addGestureRecognizer:singleTap];
        
        [self addSubview:self.scrollView];
    }
    
    [self createOrUpdateViews:self.frame fromDataListener:NO];
}

- (void)setScrollViewContentSize {
    
    if (self.scrollView) {
        BOOL landscape = [[FFTGlobalState_fluidApp__ getUiService] isOrientationLandscape];
        float height = [self.layout calculateHeightWithBoolean:landscape withFloat:self.frame.size.width withNSString:self.dataModelKeyPrefix];
        self.scrollViewContentSize = MAX(height, self.frame.size.height);
        float extraForKeyboard = 0;
        if (self.keyboardUp) {
            extraForKeyboard = self.keyboardHeight;
        }
        self.scrollView.contentSize = CGSizeMake(self.frame.size.width, self.scrollViewContentSize + extraForKeyboard);
    }
}

- (void)handleTapFromScrollGestureRecognizer:(UITapGestureRecognizer *)singleTap {
    
    [self resizeIfKeyboardUp];
    
    // Only gets called if the tap is outside of the textbox
    UIView *view = [self findFirstResponder];
    tappedOutsideWhileFocusedListener block = [self.tappedOutsideWhileFocusedListeners objectForKey:[NSValue valueWithNonretainedObject:view]];
    if (block)
        block();
}

- (void)resizeIfKeyboardUp {
    
    if (self.scrollView && self.keyboardUp) {
        self.keyboardUp = NO;
        self.scrollView.contentSize = CGSizeMake(self.scrollView.contentSize.width, self.scrollViewContentSize);
    }
}

- (void)createOrUpdateViews:(CGRect)parentBounds fromDataListener:(BOOL)fromDataListener {
    
    if (self.hasRanCleanup) {
        // hstdbc This is a safeguard to a memory leak. This is a temporary band aid. The memory leak should be found. (subview repeat inside a table)
        return;
    }
    
    self.frame = parentBounds; // If called by FluidTableViewDelegate
    
    // If scrollView
    self.scrollView.frame = CGRectMake(0, 0, parentBounds.size.width, parentBounds.size.height);
    
    CGSize size = parentBounds.size;
    
    BOOL landscape = [[FFTGlobalState_fluidApp__ getUiService] isOrientationLandscape];
    
    if (fromDataListener) {
        [self.layout invalidateCacheWithNSString:self.dataModelKeyPrefix];
    }
    
    id<JavaUtilCollection> list = [self.layout getViewsWithBoolean:landscape withDouble:size.width withDouble:size.height withNSString:self.dataModelKeyPrefix withNSString:self.viewPath];
    for (FFTViewPosition * __strong view in nil_chk(list)) {
        
        //if (![view isVisible]) {
        //    continue;
        //}
        
        CGRect bounds = CGRectMake(view->x_, view->y_, view->width_, view->height_);
        
        FFTViewBehavior *viewBehavior = view->viewBehavior_;
        
        NSString *listenerId = self.dataModelListenerId;
        if (!self.dataModelListenerId)
            listenerId = (self.rootFFView) ? self.rootFFView.layout->id__ : self.layout->id__;
        
        FFTViewBuilderInfo *info = [[FFTViewBuilderInfo alloc] init];
        info.bounds = bounds;
        info.parentBounds = parentBounds;
        info.listenerId = listenerId;
        info.dataModelKeyPrefix = self.dataModelKeyPrefix;
        info.fluidView = self;
        info.viewPath = [NSString stringWithFormat:@"%@.%@", self.viewPath, view->id__];
        info.modalView = self.modalView;
        info.fromDataListener = fromDataListener;
        info.tableView = self.tableView;
        info.precompute = [self.layout isPrecomputedPositions];
        
        UIView *uiView = [self.viewsById objectForKey:view->id__];
        if (uiView == nil && [view isVisible]) {
        //if (uiView == nil) { // If the child class wants to access this view, for animating and such, we need to create it even if its not visible
            uiView = [[[FFTGlobalState fluidApp] getFluidViewFactory] createViewWithNSString:[viewBehavior getType] withFFTViewPosition:view withId:info];
            if ([uiView isKindOfClass:[FFView class]]) {
                ((FFView *) uiView).rootFFView = self;
            }
            [self addSubview:uiView forFluidView:view dataModelPrefix:info.dataModelKeyPrefix];
            // hstdbc should this be in update only?
            [[[FFTGlobalState fluidApp] getFluidViewFactory] updateViewWithNSString:[viewBehavior getType] withId:uiView withFFTViewPosition:view withId:info];
        } else if (uiView != nil) {
            //[uiView setBounds:parentBounds];
            // hstdbc should this be in update only?
            [[[FFTGlobalState fluidApp] getFluidViewFactory] updateViewWithNSString:[viewBehavior getType] withId:uiView withFFTViewPosition:view withId:info];
        }
        [FFView styleView:uiView viewBehavior:viewBehavior dataModelPrefix:info.dataModelKeyPrefix];
    }
    
    if (!self.hasAddedConditionalListener) {
        id<JavaUtilCollection> fromList = [self.layout getConditionalKeys];
        for (NSString * __strong conditionalKey in fromList) {
            NSString *listener = [NSString stringWithFormat:@"customLayout-%d-conditional-%@", self.containerId, self.viewPath];
            [[[FFTGlobalState fluidApp] getDataModelManager] addDataChangeListenerWithNSString:conditionalKey withNSString:listener withFFTDataChangeListener:self];
        }
        self.hasAddedConditionalListener = YES;
    }
}

- (void)cleanup {
    
    if (self.hasRanCleanup) {
        return;
    }
    
    self.hasRanCleanup = YES;
    
    JavaUtilLinkedHashSet *list = (JavaUtilLinkedHashSet *) [self.layout getAllViewsToBePresentedToUI];
    for (FFTView * __strong view in nil_chk(list)) {
        FFTViewBehavior *viewBehavior = view->viewBehavior_;
        UIView *uiView = [self.viewsById objectForKey:view->id__];
        [[[FFTGlobalState fluidApp] getFluidViewFactory] cleanupViewWithNSString:[viewBehavior getType] withId:uiView];
        if ([uiView isKindOfClass:[FFView class]]) {
            [((FFView *) uiView) cleanup];
        }
    }
    NSString *listener = [NSString stringWithFormat:@"customLayout-%d-conditional-%@", self.containerId, self.viewPath];
    [[[FFTGlobalState fluidApp] getDataModelManager] removeDataChangeListenerWithNSString:listener];
    self.hasAddedConditionalListener = NO;
    
    self.viewsById = nil;
    
    self.listenToDataModelChanges = NO;
}

- (void)addSubview:(UIView *)view forFluidView:(FFTViewPosition *)fluidView dataModelPrefix:(NSString *)dataModelPrefix {

    if (self.scrollView == nil) {
        [self addSubview:view];
    } else {
        [self.scrollView addSubview:view];
    }
    [FFView styleView:view viewBehavior:[fluidView getViewBehavior] dataModelPrefix:dataModelPrefix];
    [self.viewsById setObject:view forKey:[fluidView getId]];
}

#pragma mark text field
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
    
    FFTextField *ffTextField = (FFTextField *) textField;
    BOOL enabled = [ffTextField.viewBehavior isEnabledWithNSString:ffTextField.dataModelKeyPrefix];
    return enabled;
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField {
    
    [self textFieldDidBeginEditingHelper:textField];
}

- (void)textFieldDidBeginEditingHelper:(UIView *)textField {
    
    if (self.rootFFView != nil) {
        [self.rootFFView textFieldDidBeginEditingHelper:textField];
        return;
    }

    self.currentTextField = textField;
    FFTextField *tf = (FFTextField *) textField;
    FFTViewBehaviorTextfield *vb = (FFTViewBehaviorTextfield *) [tf.view getViewBehavior];
    self.dismissCurrentKeyboardWithTap = [vb isDismissKeyboardWithTap];
    
    CGRect textFieldRect = [self.window convertRect:textField.bounds fromView:textField];
    
    float bottom;
    
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    CGFloat screenHeight;
    
    float keyboardHeight;
    UIInterfaceOrientation orientation = [[UIApplication sharedApplication] statusBarOrientation];
    if (orientation == UIInterfaceOrientationPortrait ||
        orientation == UIInterfaceOrientationPortraitUpsideDown) {
        keyboardHeight = PORTRAIT_KEYBOARD_HEIGHT;
        screenHeight = screenRect.size.height;
        bottom = textFieldRect.origin.y + textFieldRect.size.height;
    } else {
        keyboardHeight = LANDSCAPE_KEYBOARD_HEIGHT;
        screenHeight = screenRect.size.width;
        bottom = textFieldRect.origin.x + textFieldRect.size.width;
    }
    
    if (bottom < screenHeight - keyboardHeight - KEYBOARD_PADDING_TOP) {
        
        float y = textFieldRect.origin.y - self.frame.origin.y;
        
        if (!self.scrollView) {
            return;
        }
        
        if ((screenHeight - keyboardHeight - KEYBOARD_PADDING_TOP) - bottom < 50) {
            self.animatedDistance = 50;
        } else {
            self.animatedDistance = 0;
        }
        
//        // Move a little bit so user sees the screen scroll
//        if (y > 100) {
//            self.animatedDistance = 50;
//        } else if (y > 50) {
//            self.animatedDistance = 25;
//        } else {
//            // At the top, don't scroll
//            self.animatedDistance = 0;
//        }
        
    } else {
        self.animatedDistance = bottom - (screenHeight - keyboardHeight - KEYBOARD_PADDING_TOP);
    }
    
    self.keyboardHeight = keyboardHeight;
    
    if (self.scrollView) {
     
        float scrollOffset = self.scrollView.contentOffset.y;
        
        if (!self.keyboardUp) {
            self.keyboardUp = YES;
            self.scrollView.contentSize = CGSizeMake(self.scrollView.contentSize.width, self.scrollViewContentSize + keyboardHeight);
            self.scrollView.contentOffset = CGPointMake(0, self.animatedDistance + scrollOffset);
        }
    } else {
    
        CGRect viewFrame = self.frame;
        viewFrame.origin.y -= self.animatedDistance;
        
        [UIView beginAnimations:nil context:NULL];
        [UIView setAnimationBeginsFromCurrentState:YES];
        [UIView setAnimationDuration:KEYBOARD_ANIMATION_DURATION];
        
        [self setFrame:viewFrame];
        
        [UIView commitAnimations];
    }
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
    [self textFieldDidEndEditingHelper:textField];
}

- (void)textFieldDidEndEditingHelper:(UIView *)textField {
    
    if (self.rootFFView != nil) {
        [self.rootFFView textFieldDidEndEditingHelper:textField];
        return;
    }
    
    self.currentTextField = nil;
    
    if (self.scrollView) {
        
        //self.keyboardUp = NO;
        //self.scrollView.contentSize = CGSizeMake(self.scrollView.contentSize.width, self.scrollViewContentSize);
    } else {
        
        CGRect viewFrame = self.frame;
        viewFrame.origin.y += self.animatedDistance;
        
        [UIView beginAnimations:nil context:NULL];
        [UIView setAnimationBeginsFromCurrentState:YES];
        [UIView setAnimationDuration:KEYBOARD_ANIMATION_DURATION];
        
        [self setFrame:viewFrame];
        
        [UIView commitAnimations];
    }
}

- (void)addTappedOutsideWhileFocusedListener:(UIView *)view tappedOutsideWhileFocusedListener:(tappedOutsideWhileFocusedListener)listener {
    
    if (self.rootFFView != nil) {
        [self.rootFFView addTappedOutsideWhileFocusedListener:view tappedOutsideWhileFocusedListener:listener];
        return;
    }
    
    [self.tappedOutsideWhileFocusedListeners setObject:listener forKey:[NSValue valueWithNonretainedObject:view]];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    
    self.eligableForTap = YES;
    
    if (self.rootFFView != nil) {
        [self.rootFFView touchesBegan:touches withEvent:event];
        [super touchesBegan:touches withEvent:event];
        return;
    }
    
    /*
    UIView *view = [self findFirstResponder];
    UITouch *touch = [[event allTouches] anyObject];
    UIView *view2 = [touch view];
    
    if (view != nil && view != view2) {
        tappedOutsideWhileFocusedListener block = [self.tappedOutsideWhileFocusedListeners objectForKey:[NSValue valueWithNonretainedObject:view]];
        if (block)
            block();
    }*/
    
    [super touchesBegan:touches withEvent:event];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    
    if (self.eligableForTap) {
        
        if (self.viewPath) {
            FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
            [eventInfo setDataModelKeyParentWithNSString:self.dataModelKeyPrefix];
            
            NSString *viewPath = self.viewPath;
            if (self.lastViewPathTokenIsIndex) {
                int index = [self.viewPath rangeOfString:@"." options:NSBackwardsSearch].location;
                viewPath = [self.viewPath substringToIndex:index];
                NSString *indexString = [self.viewPath substringFromIndex:(index + 1)];
                [eventInfo setUserInfoWithId:[JavaLangInteger valueOfWithInt:indexString.intValue]];
            }
            
            [[[FFTGlobalState fluidApp] getEventsManager] userTappedWithNSString:viewPath withFFTActionListener_EventInfo:eventInfo];
        }
        
        //UIView *view = [self findFirstResponder];
        UIView *view = [[FFView rootFFView:self] findFirstResponder];
        UITouch *touch = [[event allTouches] anyObject];
        UIView *view2 = [touch view];
        
        if (view != nil && view != view2) {
            tappedOutsideWhileFocusedListener block = [self.tappedOutsideWhileFocusedListeners objectForKey:[NSValue valueWithNonretainedObject:view]];
            if (!block) {
                block = [((FFView *)[FFView rootFFView:self]).tappedOutsideWhileFocusedListeners objectForKey:[NSValue valueWithNonretainedObject:view]];
            }
            if (block)
                block();
        }
    }
    
    self.eligableForTap = NO;
    
    [super touchesEnded:touches withEvent:event];
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event {
    
    self.eligableForTap = NO;
    
    [super touchesCancelled:touches withEvent:event];
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    
    self.eligableForTap = NO;
    
    [super touchesMoved:touches withEvent:event];
}

- (id)findFirstResponder {
    return [FFView findFirstResponder:self];
}

- (void)setTextFieldDelegate:(id)tfd {
    for (UIView *view in [self subviews]) {
        if ([view isKindOfClass:[FFTextField class]]) {
            FFTextField *tf = (FFTextField *) view;
            tf.delegate = tfd;
        }
    }
}

+ (id)rootFFView:(UIView *)view {
    return [FFView rootFFViewHelper:view.superview];
}

+ (id)rootFFViewHelper:(UIView *)view {
    FFView *ffSuperView = nil;
    if (view.superview) {
        ffSuperView = [FFView rootFFViewHelper:view.superview];
    }
    
    if (ffSuperView == nil && [view isKindOfClass:[FFView class]]) {
        ffSuperView = (FFView *) view;
    }
    
    return ffSuperView;
}

// hstdbc refactor to utility class
+ (id)findFirstResponder:(UIView *)view {
    if (view.isFirstResponder) {
        return view;
    }
    for (UIView *subView in view.subviews) {
        id responder = [FFView findFirstResponder:subView];
        if (responder)
            return responder;
    }
    return nil;
}

// hstdbc refactor to utility class
+ (NSString *)valueFor:(FFTViewPosition *)view baseText:(NSString *)baseText dataModelKeyPrefix:(NSString *)dataModelKeyPrefix {
    if (!view->key_) {
        return baseText;
    } else {
        return [view getValueWithNSString:dataModelKeyPrefix withNSString:view->key_ withNSString:baseText];
    }
}

+ (void)setValueFor:(FFTViewPosition *)view dataModelKeyPrefix:(NSString *)dataModelKeyPrefix to:(id)value {
    if (view->key_) {
        [view setValueWithNSString:dataModelKeyPrefix withNSString:view->key_ withId:value];
    }
}

+ (void)styleView:(UIView *)view viewBehavior:(FFTViewBehavior *)viewBehavior dataModelPrefix:(NSString *)dataModelPrefix {
    
    UIColor *backgroundColor = [FFView color:[viewBehavior getBackgroundColorWithNSString:dataModelPrefix]];
    
    if (backgroundColor) {
        [view setBackgroundColor:backgroundColor];
    }
}

// hstdbc refactor to utility class
+ (UIColor *)color:(FFTColor *)ffCol {
    if (ffCol) {
        return [UIColor colorWithRed:[ffCol getRed] green:[ffCol getGreen] blue:[ffCol getBlue] alpha:[ffCol getAlpha]];
    } else {
        return nil;
    }
}

/*
- (void)willMoveToSuperview:(UIView *)newSuperview {
    if (newSuperview) {
        id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
        [[appDelegate dataNotificationService] enableDataChangeObserverForId:self.dataModelListenerId];
    } else {
        id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
        [[appDelegate dataNotificationService] disableDataChangeObserverForId:self.dataModelListenerId];
    }
}*/

- (void)grabFocusForView:(NSString *)viewId {
    
    UIView *uiView = [self.viewsById objectForKey:viewId];
    [uiView becomeFirstResponder];
}

- (void)hideKeyboard {
    
    [self endEditing:YES];
}

- (void)willMoveToWindow:(UIWindow *)newWindow {

    [self resizeIfKeyboardUp];
    
    if (self.listenToDataModelChanges) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_sync(dispatch_get_main_queue(), ^{
                if (newWindow) {
                    id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
                    [[appDelegate dataNotificationService] enableDataChangeObserverForId:self.dataModelListenerId];
                } else {
                    id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
                    [[appDelegate dataNotificationService] disableDataChangeObserverForId:self.dataModelListenerId];
                }
            });
        });
    }
}

- (void)scrollToBottomWithNSString:(NSString *)viewPath
                      withNSString:(NSString *)viewId {
    
    if (![self.viewPath isEqualToString:viewPath]) {
        return;
    }
    
    if (!viewId) {
        if (self.scrollView) {
            [self.scrollView setContentOffset:CGPointMake(0, CGFLOAT_MAX)];
        }
        return;
    }
    
    UIView *uiView = [self.viewsById objectForKey:viewId];
    if ([uiView isKindOfClass:[FFTableView class]]) {
        FFTableView *table = (FFTableView *) uiView;
        [table setContentOffset:CGPointMake(0, CGFLOAT_MAX)];
    } else {
        [NSException raise:@"Unsupported operation: scrollToBottom for view" format:@"Unsupported operation: scrollToBottom for view %@", viewId];
    }
}

@end
