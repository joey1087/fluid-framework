//
//  FFViewController.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 2/17/14.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFViewController.h"
#import "FFFluidAppDelegate.h"
#import "FluidApp.h"
#import "View.h"
#include "java/lang/Double.h"
#include "java/lang/Boolean.h"
#include "Screen.h"
#include "MenuButtonItem.h"
#include "FFTableViewDelegate.h"
#include "Constraints.h"
#include "java/util/HashMap.h"
#include "FFView.h"
#include "FFDataNotificationService.h"
#include "GlobalState.h"
#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "ViewManager.h"
#include "com/sponberg/fluid/layout/DataModelManager.h"
#include "Logger.h"
#include "Layout.h"

@interface FFViewController ()

@property (nonatomic, strong, readwrite) FFView *baseView;
@property (nonatomic, assign) BOOL viewWillRotate;
@property (nonatomic, assign) BOOL viewNeedsRefreshOnAppear;
@property (nonatomic, assign) BOOL partOfRootView;

@end

@implementation FFViewController

- (id)initWithScreenId:(NSString *)screenId partOfRootView:(BOOL)partOfRootView {
    self = [super initWithNibName:nil bundle:nil];
    if (self) {
        self.screen = [[FFTGlobalState fluidApp] getScreenWithNSString:screenId];

        if (self.screen == nil) {
            [NSException raise:@"No screen" format:@"Screen %@ is nil, did you forget to add the .txt file to Xcode?", screenId];
        }
        
        NSString *name = [[[FFTGlobalState fluidApp] getScreenWithNSString:screenId] getName];
        
        NSString *nameKey = [[[FFTGlobalState fluidApp] getScreenWithNSString:screenId] getNameKey];
        
        if (nameKey) {
            
            NSString *text = [((FFTDataModelManager *) nil_chk([((FFTFluidApp *) nil_chk(FFTGlobalState_get_fluidApp__())) getDataModelManager])) getValueWithNSString:nil withNSString:nameKey withNSString:name withNSString:nil];
            self.title = text;
        } else {
            
            self.title = name;
        }
        
        self.partOfRootView = partOfRootView;
        
        if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
            self.automaticallyAdjustsScrollViewInsets = NO;
        }
    }
    return self;
}

- (void)dealloc {
    [self.baseView cleanup];
    [self.screen screenWasRemoved];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    NSMutableArray *array = [NSMutableArray array];
    NSMutableArray *leftArray = [NSMutableArray array];
    
    for (FFTMenuButtonItem * __strong item in nil_chk([self.screen getNavigationMenuItems])) {
        
        UIBarButtonSystemItem buttonItem = 0;
        if ([item getSystemId] && ![[item getSystemId] isEqualToString:FFTMenuButtonItem_SystemItemCustom_]) {
            buttonItem = [self systemItemFor:item];
        }
        
        UIBarButtonItem *button;
        if (buttonItem != 0) {
            button = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:buttonItem target:item action:@selector(userTapped)];
        } else if ([item getIconName]) {
            
            UIImage *image;
            UIColor *buttonTextColor = [self defaultColor:@"ios-nav-bar-button-text-color"];
            if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0 || buttonTextColor == nil) {
                // If iOS 7+ (will take color from tint) or if color isn't specified
                image = [UIImage imageNamed:[item getIconName]];
            } else {
                // If iOS 6, we need to apply the color tint to the gray ourself
                image = [self tintedImageWithColorForIOS6:[item getIconName] tintColor:buttonTextColor];
            }
            
            UIImage *landscapeImage = image;
            button = [[UIBarButtonItem alloc] initWithImage:image landscapeImagePhone:landscapeImage style:UIBarButtonItemStylePlain target:item action:@selector(userTapped)];
        } else {
            button = [[UIBarButtonItem alloc] initWithTitle:[item getTitle] style:UIBarButtonItemStylePlain target:item action:@selector(userTapped)];
        }
        
        if ([item isPreferenceShowOnLeft]) {
            [leftArray addObject:button];
        } else {
            [array addObject:button];
        }
        
    }

    if ([array count] > 0) {
        self.navigationController.topViewController.navigationItem.rightBarButtonItems = array;
    }
    if ([leftArray count] > 0) {
        self.navigationController.topViewController.navigationItem.leftBarButtonItems = leftArray;
    }
    
    UIColor *textColor = nil;
    UIColor *color = [self defaultColor:@"ios-nav-bar-text-color"];
    if (color) {
        
        textColor = color;
        NSDictionary *attributes = [NSDictionary dictionaryWithObjectsAndKeys:color, UITextAttributeTextColor, nil];
        [[UIBarButtonItem appearance] setTitleTextAttributes:attributes forState:UIControlStateNormal];
    }

    color = [self defaultColor:@"ios-nav-bar-button-color"];
    if (color) {
        
        [[UIBarButtonItem appearance] setTintColor:color];
    }

    color = [self defaultColor:@"ios-nav-bar-button-text-color"];
    if (color) {
        
        [[UIBarButtonItem appearanceWhenContainedIn:[UINavigationBar class], nil] setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:color, UITextAttributeTextColor,nil] forState:UIControlStateNormal];
    }

    UIColor *subtitleTextColor = nil;
    color = [self defaultColor:@"ios-nav-bar-subtitle-text-color"];
    if (color) {
        
        subtitleTextColor = color;
    }
    
    NSString *subtitle = [self.screen getSubtitle];
    NSString *subtitleKey = [self.screen getSubtitleKey];
    
    if (subtitleKey) {
        
        subtitle = [((FFTDataModelManager *) nil_chk([((FFTFluidApp *) nil_chk(FFTGlobalState_get_fluidApp__())) getDataModelManager])) getValueWithNSString:nil withNSString:subtitleKey withNSString:subtitle withNSString:nil];
    }
    
    if ([subtitle length] > 0) {
        
        int height = self.navigationController.navigationBar.frame.size.height;
        
        UIFont* titleFont = [UIFont boldSystemFontOfSize:18];
        CGSize requestedTitleSize = [self.title sizeWithFont:titleFont];
        CGFloat widthA = MIN(200, requestedTitleSize.width);
        
        UIFont* subtitleFont = [UIFont boldSystemFontOfSize:13];
        requestedTitleSize = [subtitle sizeWithFont:subtitleFont];
        CGFloat widthB = MIN(200, requestedTitleSize.width);
        
        CGFloat width = MAX(widthA, widthB);
        
        CGRect headerTitleSubtitleFrame = CGRectMake(0, 0, width, height);
        UIView* headerTitleSubtitleView = [[UILabel alloc] initWithFrame:headerTitleSubtitleFrame];
        headerTitleSubtitleView.autoresizesSubviews = YES;
        headerTitleSubtitleView.backgroundColor = [UIColor clearColor];
        
        CGRect titleFrame = CGRectMake(0, 2, width, height / 2);
        UILabel *label = [[UILabel alloc] initWithFrame:titleFrame];
        label.font = titleFont;
        label.textAlignment = NSTextAlignmentCenter;
        label.adjustsFontSizeToFitWidth = YES;
        label.text = self.title;
        label.backgroundColor = [UIColor clearColor];

        if (textColor) {
            label.textColor = textColor;
        }
        
        CGRect subtitleFrame = CGRectMake(0, height / 2, width, height / 2);
        UILabel *subLabel = [[UILabel alloc] initWithFrame:subtitleFrame];
        subLabel.font = subtitleFont;
        subLabel.textAlignment = NSTextAlignmentCenter;
        subLabel.adjustsFontSizeToFitWidth = YES;
        subLabel.text = subtitle;
        subLabel.backgroundColor = [UIColor clearColor];
        
        if (subtitleTextColor) {
            
            subLabel.textColor = subtitleTextColor;
        } else if (textColor) {
            
            subLabel.textColor = textColor;
        }
        
        [headerTitleSubtitleView addSubview:label];
        [headerTitleSubtitleView addSubview:subLabel];
        
        headerTitleSubtitleView.autoresizingMask = (UIViewAutoresizingFlexibleLeftMargin |
                                                     UIViewAutoresizingFlexibleRightMargin |
                                                     UIViewAutoresizingFlexibleTopMargin |
                                                     UIViewAutoresizingFlexibleBottomMargin);
        
        self.navigationController.topViewController.navigationItem.titleView = headerTitleSubtitleView;
    } else if (textColor) {
        
        int height = self.navigationController.navigationBar.frame.size.height;
        
        UIFont* titleFont = [UIFont boldSystemFontOfSize:18];
        CGSize requestedTitleSize = [self.title sizeWithFont:titleFont];
        CGFloat width = MIN(200, requestedTitleSize.width);

        CGRect titleFrame = CGRectMake(0, 2, width, height);
        UILabel *label = [[UILabel alloc] initWithFrame:titleFrame];
        label.font = titleFont;
        label.textAlignment = NSTextAlignmentCenter;
        label.adjustsFontSizeToFitWidth = YES;
        label.text = self.title;
        label.backgroundColor = [UIColor clearColor];
        
        if (textColor) {
            label.textColor = textColor;
        }

        self.navigationController.topViewController.navigationItem.titleView = label;
    }
    
    [self refreshMenuButtons];
    
    self.viewNeedsRefreshOnAppear = NO;
    
    [self createOrUpdateBaseView];
}

- (void)refreshMenuButtons {
    
    int index = 0;
    int indexLeft = 0;
    for (FFTMenuButtonItem * __strong item in nil_chk([self.screen getNavigationMenuItems])) {

        UIBarButtonItem *button;
        
        if ([item isPreferenceShowOnLeft]) {
            button = [self.navigationController.topViewController.navigationItem.leftBarButtonItems objectAtIndex:indexLeft];
            indexLeft++;
        } else {
            button = [self.navigationController.topViewController.navigationItem.rightBarButtonItems objectAtIndex:index];
            index++;
        }
        button.enabled = [item isEnabled];
    }
}

- (BOOL)prefersStatusBarHidden {
    return self.partOfRootView && ![self.screen isShowStatusBar];
}

- (void)viewWillAppear:(BOOL)animated {
    
    IOSObjectArray *property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-nav-bar" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
    NSString *colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
    if (colorProperty) {
        FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
        UIColor *color = [FFView color:fftColor];
        if ([self.navigationController.navigationBar respondsToSelector:@selector(setBarTintColor:)]) {
            self.navigationController.navigationBar.barTintColor = color;
        } else {
            self.navigationController.navigationBar.tintColor = color;
        }
        
        self.navigationController.navigationBar.translucent = NO;
    }
    
    if (self.partOfRootView && [self.screen isShowTabBar]) {
        [self.tabBarController.tabBar setHidden:NO];
    } else {
        [self.tabBarController.tabBar setHidden:YES];
    }
    
    if ([self.screen isShowNavigationBar]) {
        [self.navigationController setNavigationBarHidden:NO];
    } else {
        [self.navigationController setNavigationBarHidden:YES];
    }
    
    if ([self.screen isShowStatusBar]) {
        [UIApplication sharedApplication].statusBarHidden = NO;
    } else {
        [UIApplication sharedApplication].statusBarHidden = YES;
    }

    if (self.viewNeedsRefreshOnAppear) {
        // Setting the baseView frame triggers viewWillLayoutSubviews, which will call createOrUpdateView
        CGRect bounds = [self computeSizeOfView];
        self.baseView.frame = CGRectMake(0, bounds.origin.y, bounds.size.width, bounds.size.height);
    }
    self.viewNeedsRefreshOnAppear = NO;
    
    //id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
    //[[appDelegate dataNotificationService] enableDataChangeObserverForId:[self.screen getScreenId]];
    
    [self refreshMenuButtons];
    
    [super viewWillAppear:animated];
    [self.screen screenWillAppear];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self.screen screenDidAppear];
}

- (void)viewWillDisappear:(BOOL)animated {
    
    self.viewNeedsRefreshOnAppear = YES;
    
    //id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
    //[[appDelegate dataNotificationService] disableDataChangeObserverForId:[self.screen getScreenId]];
}

- (void)viewDidDisappear:(BOOL)animated {

    [super viewDidDisappear:animated];
    
    [super viewWillDisappear:animated];
    
    [self.screen screenDidDisappear];
    //[self.baseView cleanup]; hstdbc can't do this because the view isn't gone
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    self.viewWillRotate = YES;
    [super willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation {
    [super didRotateFromInterfaceOrientation:fromInterfaceOrientation];
    self.viewWillRotate = NO;
    // Setting the baseView frame triggers viewWillLayoutSubviews, which will call createOrUpdateView
    CGRect bounds = [self computeSizeOfView];
    self.baseView.frame = CGRectMake(0, bounds.origin.y, bounds.size.width, bounds.size.height);
}

- (void)viewDidLayoutSubviews {
    [self createOrUpdateBaseView];
    [super viewDidLayoutSubviews];
}

- (void)createOrUpdateBaseView {
    
    CGRect bounds = [self computeSizeOfView];
    
    if (self.baseView == nil) {
        self.baseView = [[FFView alloc] initWithFrame:bounds viewPath:[self.screen getScreenId] layout:[self.screen getLayout] rootFFView:nil inTableView:nil];
        [self.view addSubview:self.baseView];
        [self.baseView createOrLayoutViews];
    }
    
    self.baseView.frame = bounds;
}

- (CGRect)computeSizeOfView {
    
    CGSize size = CGSizeMake(self.view.bounds.size.width, self.view.bounds.size.height);
    
    CGRect statusBar = [self statusBarFrameViewRect:self.view];
    
    float yStart = 0;
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0) {
        yStart = statusBar.size.height + statusBar.origin.y; // statusBar.origin.y is -20 when in-call status bar is showing
        size.height -= yStart;
    
        if ([self.screen isShowNavigationBar]) {
            CGRect frame = self.navigationController.navigationBar.frame;
            size.height -= frame.size.height;
            yStart += frame.size.height;
        }
    
        if (self.partOfRootView && [self.screen isShowTabBar]) {
            size.height -= self.tabBarController.tabBar.frame.size.height;
        }
    }
    
    return CGRectMake(0, yStart, size.width, size.height);
}

- (CGRect)statusBarFrameViewRect:(UIView*)view {
    
    CGRect statusBarFrame = [[UIApplication sharedApplication] statusBarFrame];
    return [self rect:statusBarFrame toView:view];
}


- (CGRect)rectFromView:(UIView *)fromView toView:(UIView *)view {
    return [self rect:fromView.frame toView:view];
}

- (CGRect)rect:(CGRect)frame toView:(UIView *)view {
    
    CGRect statusBarWindowRect = [view.window convertRect:frame fromWindow:nil];
    
    CGRect statusBarViewRect = [view convertRect:statusBarWindowRect fromView:nil];
    
    return statusBarViewRect;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (UIBarButtonSystemItem)systemItemFor:(FFTMenuButtonItem *)item {
    
    UIBarButtonSystemItem buttonItem = UIBarButtonSystemItemAdd;

    if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemDone_])
        buttonItem = UIBarButtonSystemItemDone;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemCancel_])
        buttonItem = UIBarButtonSystemItemCancel;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemEdit_])
        buttonItem = UIBarButtonSystemItemEdit;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemSave_])
        buttonItem = UIBarButtonSystemItemSave;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemAdd_])
        buttonItem = UIBarButtonSystemItemAdd;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemFlexibleSpace_])
        buttonItem = UIBarButtonSystemItemFlexibleSpace;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemFixedSpace_])
        buttonItem = UIBarButtonSystemItemFixedSpace;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemCompose_])
        buttonItem = UIBarButtonSystemItemCompose;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemReply_])
        buttonItem = UIBarButtonSystemItemReply;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemAction_])
        buttonItem = UIBarButtonSystemItemAction;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemOrganize_])
        buttonItem = UIBarButtonSystemItemOrganize;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemBookmarks_])
        buttonItem = UIBarButtonSystemItemBookmarks;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemSearch_])
        buttonItem = UIBarButtonSystemItemSearch;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemRefresh_])
        buttonItem = UIBarButtonSystemItemRefresh;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemStop_])
        buttonItem = UIBarButtonSystemItemStop;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemCamera_])
        buttonItem = UIBarButtonSystemItemCamera;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemTrash_])
        buttonItem = UIBarButtonSystemItemTrash;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemPlay_])
        buttonItem = UIBarButtonSystemItemPlay;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemPause_])
        buttonItem = UIBarButtonSystemItemPause;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemRewind_])
        buttonItem = UIBarButtonSystemItemRewind;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemFastForward_])
        buttonItem = UIBarButtonSystemItemFastForward;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemUndo_])
        buttonItem = UIBarButtonSystemItemUndo;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemRedo_])
        buttonItem = UIBarButtonSystemItemRedo;
    else if ([item->systemId_ isEqualToString:FFTMenuButtonItem_SystemItemPageCurl_])
        buttonItem = UIBarButtonSystemItemPageCurl;
    
    return buttonItem;
}

- (UIImage *)tintedImageWithColorForIOS6:(NSString *)imageName tintColor:(UIColor *)tintColor {
    
    UIImage *image = [UIImage imageNamed:imageName];
    
    UIGraphicsBeginImageContextWithOptions(image.size, NO, [[UIScreen mainScreen] scale]);
    CGContextRef context = UIGraphicsGetCurrentContext();
    
    CGContextTranslateCTM(context, 0, image.size.height);
    CGContextScaleCTM(context, 1.0, -1.0);
    
    CGRect rect = CGRectMake(0, 0, image.size.width, image.size.height);
    
    // draw alpha-mask
    CGContextSetBlendMode(context, kCGBlendModeNormal);
    CGContextDrawImage(context, rect, image.CGImage);
    
    // draw tint color, preserving alpha values of original image
    CGContextSetBlendMode(context, kCGBlendModeSourceIn);
    [tintColor setFill];
    CGContextFillRect(context, rect);
    
    UIImage *coloredImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return coloredImage;
}

- (UIColor *)defaultColor:(NSString *)name {

    IOSObjectArray *property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", name } count:3 type:[IOSClass classWithClass:[NSObject class]]];
    NSString *colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
    UIColor *textColor = nil;
    if (colorProperty) {
        FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
        textColor = [FFView color:fftColor];
    }
    
    return textColor;
}

@end
