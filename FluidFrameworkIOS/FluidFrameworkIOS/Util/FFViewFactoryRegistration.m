//
//  FFViewFactoryRegistration.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 10/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFViewFactoryRegistration.h"
#import "FluidApp.h"
#import "FluidViewFactory.h"
#import "ViewBehavior.h"
#include "ViewBehavior.h"
#include "ViewBehaviorLabel.h"
#include "ViewBehaviorButton.h"
#include "ViewBehaviorTable.h"
#include "ViewBehaviorImage.h"
#include "ViewBehaviorSubview.h"
#include "ViewBehaviorSubviewRepeat.h"
#include "ViewBehaviorWebView.h"
#include "ViewBehaviorURLWebView.h"
#include "ViewBehaviorTextfield.h"
#include "ViewBehaviorSearchbar.h"
#include "ViewBehaviorSpace.h"
#include "ViewBehaviorSegmentedControl.h"
#include "View.h"
#include "FFFluidAppDelegate.h"
#include "Layout.h"
#include "FFView.h"
#include "java/lang/Double.h"
#include "java/lang/Boolean.h"
#include "java/lang/Integer.h"
#include "java/util/ArrayList.h"
#include "FFTableViewDelegate.h"
#include "ImageManager.h"
#include "FFWebView.h"
#include "FFTextField.h"
#import "GlobalState.h"
#import "FFTableView.h"
#import "FFSearchBar.h"
#import "FFViewContainer.h"
#import "FFImageView.h"
#import "FFTextView.h"
#import "FFButton.h"
#import "EventsManager.h"
#import "ActionListener.h"
#import "FFSegmentedControl.h"
#import "AttributedText.h"
#import "FFSubviewRepeat.h"
#import "ViewPosition.h"
#import "FFSpace.h"
#import "FFLabel.h"
#import "ViewPosition.h"
#import "PrecomputeLayoutManager.h"
#include "UIService.h"
#include "ViewManager.h"
#include "com/sponberg/fluid/layout/DataModelManager.h"



@interface ButtonBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface LabelBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface SpaceBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface TableBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface ImageBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface SubviewBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface SubviewRepeatBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface WebViewBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface URLWebViewBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end
@interface TextfieldBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder, UITextFieldDelegate, UITextViewDelegate>
@end
@interface SearchbarBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder, UISearchBarDelegate>
@end
@interface SegmentedControlBuilder : NSObject<FFTFluidViewFactory_FluidViewBuilder>
@end

@implementation FFRectSizeWithFont

@end

@implementation FFViewFactoryRegistration

+ (void)registerViews:(FFTFluidApp *)fluidApp {
    
    FFTFluidViewFactory *factory = [fluidApp getFluidViewFactory];
    
    [factory registerViewWithNSString:FFTViewBehavior_button_ withFFTFluidViewFactory_FluidViewBuilder:[[ButtonBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_label_ withFFTFluidViewFactory_FluidViewBuilder:[[LabelBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_space_ withFFTFluidViewFactory_FluidViewBuilder:[[SpaceBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_table_ withFFTFluidViewFactory_FluidViewBuilder:[[TableBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_image_ withFFTFluidViewFactory_FluidViewBuilder:[[ImageBuilder alloc] init]];

    [factory registerViewWithNSString:FFTViewBehavior_subview_ withFFTFluidViewFactory_FluidViewBuilder:[[SubviewBuilder alloc] init]];

    [factory registerViewWithNSString:FFTViewBehavior_subviewRepeat_ withFFTFluidViewFactory_FluidViewBuilder:[[SubviewRepeatBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_webview_ withFFTFluidViewFactory_FluidViewBuilder:[[WebViewBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_urlWebview_ withFFTFluidViewFactory_FluidViewBuilder:[[URLWebViewBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_textfield_ withFFTFluidViewFactory_FluidViewBuilder:[[TextfieldBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_searchbar_ withFFTFluidViewFactory_FluidViewBuilder:[[SearchbarBuilder alloc] init]];
    
    [factory registerViewWithNSString:FFTViewBehavior_segmentedControl_ withFFTFluidViewFactory_FluidViewBuilder:[[SegmentedControlBuilder alloc] init]];
    
}

+ (void)addDataChangeObserverFor:(FFTView *)view info:(FFTViewBuilderInfo *)info listenForChildren:(BOOL)listenForChildren block:(observerBlock)block blockDataRemoved:(observerBlockDataRemoved)blockRemoved {
    [FFViewFactoryRegistration addDataChangeObserverFor:info.dataModelKeyPrefix dataModelKey:view->key_ listenerId:info.listenerId listenForChildren:listenForChildren block:block blockDataRemoved:blockRemoved];
}

+ (void)addDataChangeObserverFor:(NSString *)prefix dataModelKey:(NSString *)dataModelKey listenerId:(NSString *)listenerId listenForChildren:(BOOL)listenForChildren block:(observerBlock)block blockDataRemoved:(observerBlockDataRemoved)blockRemoved {
    
    if (dataModelKey) {
        id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
        [[appDelegate dataNotificationService] addDataChangeObserverFor:prefix
                                                                    key:dataModelKey
                                                             observerId:listenerId
                                                      listenForChildren:listenForChildren
                                                                  block:block
                                                       blockDataRemoved:blockRemoved];
    }
}

+ (void)removeDataChangeObserverFor:(NSString *)listenerId {

    id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
    [[appDelegate dataNotificationService] removeDataChangeObserverFor:listenerId];
}

+ (NSAttributedString *)createAttributedString:(FFTAttributedText *)attText size:(float)pointSize defaultColor:(UIColor *)defaultColor fontName:(NSString*)fontName {
    
    NSMutableAttributedString *attrString = [[NSMutableAttributedString alloc] initWithString:[attText getText]];
    
    [attrString beginEditing];
    
    [attrString addAttribute:NSFontAttributeName
                       value:[UIFont systemFontOfSize:pointSize]
                       range:NSMakeRange(0, [[attText getText] length])];
    
    if (fontName) {
        UIFont* font = [UIFont fontWithName:fontName size:pointSize];
        [attrString addAttribute:NSFontAttributeName
                           value:font
                           range:NSMakeRange(0, [[attText getText] length])];
    }
    
    for (FFTAttributedText_Attribute *att in [attText getAttributes]) {
        NSRange range = NSMakeRange([att getStartIndex], [att getEndIndex] - [att getStartIndex]);
        if ([att isBold]) {
            [attrString addAttribute:NSFontAttributeName
                               value:[UIFont boldSystemFontOfSize:pointSize]
                               range:range];
        
        } else if ([att isItalic]) {
            [attrString addAttribute:NSFontAttributeName
                               value:[UIFont italicSystemFontOfSize:pointSize]
                               range:range];
        }
        if ([att isUnderline]) {
            [attrString addAttribute:NSUnderlineStyleAttributeName
                               value:@(NSUnderlineStyleSingle)
                               range:range];
        }
        if ([att getBackgroundColor] != nil) {
            UIColor *textColor = [FFView color:[att getBackgroundColor]];
            [attrString addAttribute:NSBackgroundColorAttributeName value:textColor range:range];
        }
        if ([att getColor] != nil) {
            UIColor *textColor = [FFView color:[att getColor]];
            [attrString addAttribute:NSForegroundColorAttributeName value:textColor range:range];
        } else if (defaultColor != nil) {
            [attrString addAttribute:NSForegroundColorAttributeName value:defaultColor range:range];
        }
    }
    
    if ([[attText getAttributes] size] == 0 && defaultColor != nil) {
        NSRange range = NSMakeRange(0, [[attText getText] length]);
        [attrString addAttribute:NSForegroundColorAttributeName value:defaultColor range:range];
    }
    
    [attrString endEditing];
    
    return attrString;
}

+ (FFRectSizeWithFont *)computeHeightOfText:(FFTAttributedText *)attText width:(float)maxWidth height:(float)maxHeight fontName:(NSString *)fontName fontSizeInUnits:(float)fontSizeInUnits minFontSizeInUnits:(float)minFontSizeInUnits maxFontSizeInUnits:(float)maxFontSizeInUnits defaultColor:(UIColor *)defaultColor {

    if (fontName == nil) {
        fontName = [UIFont systemFontOfSize:10].fontName;
    }

    float kFontSizeExploreStepDown = .25;
    
    float kWidthSizeExploreStepDown = .25;
    
    BOOL changeFontSize;
    if (fontSizeInUnits == 0) {
        changeFontSize = YES;
        fontSizeInUnits = [[[FFTGlobalState fluidApp] getDefaultWithNSString:@"font" withNSString:@"size"] floatValue];
    } else {
        // Don't allow font to resize. Instead, this will adjust the width to be more narrow and let the height grow.
        // This means that the text could clip at the end, because the actual height allowed could be less than the
        // height needed.
        // For heights that are computed, this should find the exact same size found during the computation phase.
        changeFontSize = NO;
    }
    
    float minimumSizeUnits;
    if (minFontSizeInUnits == 0) {
        minimumSizeUnits = [[[FFTGlobalState fluidApp] getDefaultWithNSString:@"font" withNSString:@"minimum-size"] floatValue];
    } else {
        minimumSizeUnits = minFontSizeInUnits;
    }
    
    if (maxFontSizeInUnits > 0 && fontSizeInUnits > maxFontSizeInUnits) {
        fontSizeInUnits = maxFontSizeInUnits;
    }
    
    if (fontSizeInUnits < minimumSizeUnits) {
        fontSizeInUnits = minimumSizeUnits;
    }
    
    float origMaxWidth = maxWidth;
    
    int itersWithNotChangingFontSize = 0;
    
    CGRect fSize;
    NSAttributedString *attString;
    while (fontSizeInUnits >= minimumSizeUnits) {
        
        float fontSize = [[FFTGlobalState fluidApp] unitsToFontPointsWithDouble:fontSizeInUnits];
        attString = [FFViewFactoryRegistration createAttributedString:attText size:fontSize defaultColor:defaultColor fontName:fontName];
        
        fSize = [attString boundingRectWithSize:CGSizeMake(maxWidth, CGFLOAT_MAX) options:(NSStringDrawingUsesLineFragmentOrigin|NSStringDrawingUsesFontLeading) context:nil];
        fSize.size.width = ceil(fSize.size.width);
        fSize.size.height = ceil(fSize.size.height);
        
        if (!changeFontSize && (fSize.size.width <= origMaxWidth || itersWithNotChangingFontSize > 20)) {
            // If not changing font size, get the width < original max width
            // If we haven't been able to find it after 20 tries, give up
            break;
        }
        
        if (fSize.size.width <= origMaxWidth && fSize.size.height <= maxHeight) {
            break;
        }
        
        if (changeFontSize) {
            fontSizeInUnits -= kFontSizeExploreStepDown;
        } else {
            maxWidth -= kWidthSizeExploreStepDown;
        }
        
        itersWithNotChangingFontSize++;
    }

    FFRectSizeWithFont *sizeWithFont = [[FFRectSizeWithFont alloc] init];
    sizeWithFont.bounds = fSize;
    sizeWithFont.attributedString = attString;
    return sizeWithFont;
}

+ (UIFont *)fontFor:(NSString *)name sizeInUnits:(float)sizeInUnits {
    float fontSize = [[FFTGlobalState fluidApp] unitsToFontPointsWithDouble:sizeInUnits];
    return [UIFont fontWithName:name size:fontSize];
}

@end

@implementation FFTViewBuilderInfo
@end

@implementation ButtonBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    
    FFTViewBehaviorButton *viewBehavior = (FFTViewBehaviorButton *) view->viewBehavior_;
    
    FFButton *button = [FFButton buttonWithType:UIButtonTypeSystem frame:info.bounds imageInButton:[viewBehavior getImage]];
    button.viewPath = info.viewPath;
    button.dataModelKeyParent = info.dataModelKeyPrefix;
    button.dataModelKey = [view getKey];
    button.modalView = info.modalView;
    button.viewId = [view getId];
    button.dataModelListenerId = (info.fluidView.listenToDataModelChanges) ? info.listenerId : nil;
    
    if ([viewBehavior getBorderSize].doubleValue > 0) {
        [button.button.layer setBorderWidth:[viewBehavior getBorderSize].floatValue];
        [button.button.layer setBorderColor:[[FFView color:viewBehavior->borderColor_] CGColor]];
    }
    
    if ([viewBehavior getImage]) {
        button.imageWidth = [viewBehavior getImageWidth].floatValue;
        button.imageHeight = [viewBehavior getImageHeight].floatValue;
    }
    
    button.button.contentHorizontalAlignment = UIControlContentHorizontalAlignmentCenter;
    
    [button.button addTarget:button action:@selector(userTapped) forControlEvents:UIControlEventTouchUpInside];
    
    button.button.userInteractionEnabled = YES;
    
     __weak typeof(self) weakSelf = self;
    if (info.fluidView.listenToDataModelChanges) {
        [FFViewFactoryRegistration addDataChangeObserverFor:info.dataModelKeyPrefix dataModelKey:view->key_ listenerId:button.dataModelListenerId
                                          listenForChildren:NO
                                                      block:^(NSString *key, NSArray *subkeys) {
                                                          [weakSelf updateText:button.button view:view baseText:viewBehavior->text_ dataModelPrefix:info.dataModelKeyPrefix];
                                                      }
                                           blockDataRemoved:nil];
    }
    
    return button;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {

    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFButton *button = fluidView;
    button.frame = bounds;
    
    if (![view isVisible]) {
        button.hidden = YES;
        return;
    } else {
        button.hidden = NO;
    }
    
    FFTViewBehaviorButton *viewBehavior = (FFTViewBehaviorButton *) view->viewBehavior_;

    [self updateText:button.button view:view baseText:viewBehavior->text_ dataModelPrefix:info.dataModelKeyPrefix];
    
    if ([viewBehavior getBackgroundImage]) {
        NSString *imageName = [[[FFTGlobalState fluidApp] getImageManager] getImageNameWithNSString:[viewBehavior getBackgroundImage] withInt:bounds.size.width withInt:bounds.size.height];
        [button.button setBackgroundImage:[UIImage imageNamed:imageName] forState:UIControlStateNormal];
    }

    if ([viewBehavior getImage]) {

        NSString *imageName = [[[FFTGlobalState fluidApp] getImageManager] getImageNameWithNSString:[viewBehavior getImage] withInt:(bounds.size.width / 2) withInt:bounds.size.height];
        
        UIImage *image = [UIImage imageNamed:imageName];
        [button.imageView setImage:image];
        
        float width = [viewBehavior getImageWidth].floatValue;
        float height = [viewBehavior getImageHeight].floatValue;
        
        float space = [viewBehavior getImageSpace].floatValue;
        
        CGRect frame = button.button.titleLabel.frame;
        
        float widthImageAndText = frame.size.width + space + width;
        float buttonWidth = bounds.size.width;

        float x = buttonWidth / 2 - widthImageAndText / 2;
        
        float y = (button.frame.size.height - height) / 2;
        
        float textInsetX = (x + width + space) - (buttonWidth / 2 - frame.size.width / 2);
        
        button.imageX = x;
        button.imageY = y;
        
        [button.button setTitleEdgeInsets:UIEdgeInsetsMake(0.0, textInsetX * 2, 0.0, 0.0)]; // *2 because center aligned
    }
    
    UIColor *textColor = [FFView color:viewBehavior->textColor_];
    if (textColor) {
        [button.button setTitleColor:textColor forState:UIControlStateNormal];
    }
    
    // TODO adjust font size to fit
}

- (void)updateText:(UIButton *)button view:(FFTViewPosition *)view baseText:(NSString *)baseText dataModelPrefix:(NSString *)dataModelPrefix {
    
    NSString *text = [FFView valueFor:view baseText:baseText dataModelKeyPrefix:dataModelPrefix];
    
    FFTAttributedText *attText = [[FFTAttributedText alloc] initWithNSString:text];
    
    FFTViewBehaviorButton *viewBehavior = (FFTViewBehaviorButton *) view->viewBehavior_;

    [viewBehavior->fontSize_ doubleValue];
    
    float maxHeight = button.frame.size.height / 2;
    
    UIColor *textColor = [FFView color:viewBehavior->textColor_];
    
    FFRectSizeWithFont *sizeWithFont = [FFViewFactoryRegistration computeHeightOfText:attText width:button.frame.size.width height:maxHeight fontName:button.font.fontName fontSizeInUnits:[viewBehavior->fontSize_ doubleValue] minFontSizeInUnits:0 maxFontSizeInUnits:0 defaultColor:textColor];

    [button setAttributedTitle:sizeWithFont.attributedString forState:UIControlStateNormal];
    //[button setTitle:[attText getText] forState:UIControlStateNormal];
}

- (void)cleanupFluidViewWithId:(id)fluidView {
    FFButton *vc = fluidView;
    if (vc.dataModelListenerId) {
        [FFViewFactoryRegistration removeDataChangeObserverFor:vc.dataModelListenerId];
    }
}

@end

@implementation LabelBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
 
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    CGRect labelFrame = CGRectMake(0, 0, bounds.size.width, bounds.size.height);
    
    FFViewContainer *viewContainer = [[FFViewContainer alloc] initWithFrame:bounds];
    viewContainer.backgroundColor = [UIColor clearColor];
    viewContainer.dataModelListenerId = (info.fluidView.listenToDataModelChanges) ? info.listenerId : nil;
    
    FFTViewBehaviorLabel *viewBehavior = (FFTViewBehaviorLabel *) view->viewBehavior_;
    
    if ([viewBehavior getCornerRadius]) {
        viewContainer.layer.cornerRadius = [[viewBehavior getCornerRadius] intValue];
        viewContainer.layer.masksToBounds = YES;
    }
    
    if ([viewBehavior getBorderSize]) {
        viewContainer.layer.borderWidth = [[viewBehavior getBorderSize] floatValue];
        viewContainer.layer.borderColor = [[FFView color:[viewBehavior getBorderColor]] CGColor];
    }
    
    FFLabel *label = [[FFLabel alloc] initWithFrame:labelFrame];
    label.backgroundColor = [UIColor clearColor];
    
    [viewContainer addSubview:label];
    
    label.textAlignment = NSTextAlignmentCenter;
    
    __weak typeof(self) weakSelf = self;

    if (info.fluidView.listenToDataModelChanges) {
        [FFViewFactoryRegistration addDataChangeObserverFor:info.dataModelKeyPrefix dataModelKey:view->key_ listenerId:viewContainer.dataModelListenerId
                                          listenForChildren:NO
                                                      block:^(NSString *key, NSArray *subkeys) {
                                                          [weakSelf updateLabel:label for:view info:info];
                                                      }
                                           blockDataRemoved:nil];
    }
    
    if ([[[FFTGlobalState fluidApp] getEventsManager] isListeningForTapAtWithNSString:info.viewPath]) {
        
        FFTViewBehaviorLabel *viewBehavior = (FFTViewBehaviorLabel *) view->viewBehavior_;
        
        viewContainer.handleTap = YES;
        viewContainer.viewPath = info.viewPath;
        viewContainer.dataModelKeyParent = info.dataModelKeyPrefix;
        viewContainer.dataModelKey = [view getKey];
        viewContainer.backgroundColorPressed = [FFView color:viewBehavior->backgroundColorPressed_];
        viewContainer.viewBehavior = viewBehavior;
    }
    
    return viewContainer;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    UIView *viewContainer = fluidView;
    
    FFLabel *label = [viewContainer.subviews firstObject];
    
    if (![view isVisible]) {
        label.hidden = YES;
        viewContainer.hidden = YES;
        return;
    } else {
        label.hidden = NO;
        viewContainer.hidden = NO;
    }
    
    CGRect labelFrame = CGRectMake(0, 0, bounds.size.width, bounds.size.height);
    
    FFTViewBehaviorLabel *viewBehavior = (FFTViewBehaviorLabel *) view->viewBehavior_;
    
    if ([[viewBehavior getAlign] isEqualToString:FFTViewBehaviorBaseLabel_kAlignLeft_]) {
        label.textAlignment = NSTextAlignmentLeft;
    } else if ([[viewBehavior getAlign] isEqualToString:FFTViewBehaviorBaseLabel_kAlignRight_]) {
        label.textAlignment = NSTextAlignmentRight;
    } else if ([[viewBehavior getAlign] isEqualToString:FFTViewBehaviorBaseLabel_kAlignCenter_]) {
        label.textAlignment = NSTextAlignmentCenter;
    }
    
    viewContainer.frame = bounds;
    label.frame = labelFrame;
    
    [self updateLabel:label for:view info:info];
}

- (void)updateLabel:(UILabel *)label for:(FFTViewPosition *)view info:(FFTViewBuilderInfo *)info {
    
    FFTViewBehaviorLabel *viewBehavior = (FFTViewBehaviorLabel *) view->viewBehavior_;

    // hstdbc TODO: view gets the value from the layout, layouts can be reused between views if they are not for screens. So we have to get the dataModel from somewhere else.
    NSString *text = [FFView valueFor:view baseText:viewBehavior->text_ dataModelKeyPrefix:info.dataModelKeyPrefix];
    
    FFTAttributedText *attText = [[FFTAttributedText alloc] initWithNSString:text];
    label.text = [attText getText];
    
    UIColor *textColor = [FFView color:viewBehavior->textColor_];
    UIColor *unknownTextColor = [FFView color:viewBehavior->unknownTextColor_];
    if (viewBehavior->unknownText_ && viewBehavior->unknownTextColor_ && [text isEqualToString:viewBehavior->unknownText_]) {
        [label setTextColor:unknownTextColor];
    } else if (textColor) {
        [label setTextColor:textColor];
    } else {
        [label setTextColor:nil];
    }
    
    if ([viewBehavior getFontFamilyName]) {
        NSString* fontName = [viewBehavior getFontFamilyName];
        
        if ([viewBehavior getFontStyle]) {
            fontName = [NSString stringWithFormat:@"%@-%@", fontName, [viewBehavior getFontStyle]];
        }
        UIFont* font = [UIFont fontWithName:fontName size:[viewBehavior getFontSize].doubleValue];
        [label setFont:font];
    }
    
    [self sizeAndPositionLabel:label fontSize:[viewBehavior->fontSize_ doubleValue] minFontSize:[viewBehavior->minFontSize_ doubleValue] maxFontSize:[viewBehavior->maxFontSize_ doubleValue]parentFrame:info.bounds verticalAlign:viewBehavior->verticalAlign_ attributedText:attText];
}

- (void)sizeAndPositionLabel:(UILabel *)label fontSize:(float)fontSizeInUnits minFontSize:(float)minFontSizeInUnits maxFontSize:(float)maxFontSizeInUnits parentFrame:(CGRect)parentFrame verticalAlign:(NSString *)verticalAlign attributedText:(FFTAttributedText *)attText {
    
    label.numberOfLines = 0;
    
    float maxHeight = label.frame.size.height;
    
    FFRectSizeWithFont *sizeWithFont = [FFViewFactoryRegistration computeHeightOfText:attText width:label.frame.size.width height:maxHeight fontName:label.font.fontName fontSizeInUnits:fontSizeInUnits minFontSizeInUnits:minFontSizeInUnits maxFontSizeInUnits:maxFontSizeInUnits defaultColor:nil];
    CGRect fSize = sizeWithFont.bounds;
    label.attributedText = sizeWithFont.attributedString;

    float adjust = 0; // align top
    if ([verticalAlign isEqualToString:@"middle"]) {
        adjust = parentFrame.size.height / 2 - fSize.size.height / 2;
    } else if ([verticalAlign isEqualToString:@"bottom"]) {
        adjust = parentFrame.size.height - fSize.size.height;
    }
    
    /*
     * This is because the the computedHeight might be bigger
     * than the bounds.height due to minFontSize is not small
     * enough to fit the text within the bounds without increasing
     * the height. When that happens the computedHeight will be
     * bigger than the bounds height as text is wrapped, which will
     * make the adjustY be less than 0 if the verticalALign is set
     * to "middle"
     */
    if (adjust < 0) {
        adjust = 0;
    }
    
    label.frame = CGRectMake(0, adjust, label.frame.size.width, MIN(label.frame.size.height, fSize.size.height));
}

- (void)cleanupFluidViewWithId:(id)fluidView {
    FFViewContainer *vc = fluidView;
    if (vc.dataModelListenerId) {
        [FFViewFactoryRegistration removeDataChangeObserverFor:vc.dataModelListenerId];
    }
}

@end

@implementation SpaceBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFSpace *space = [[FFSpace alloc] initWithFrame:bounds];
    space.backgroundColor = [UIColor clearColor];
    
    FFTViewBehaviorSpace *viewBehavior = (FFTViewBehaviorSpace *) view->viewBehavior_;
    
    if ([viewBehavior getCornerRadius]) {
        space.layer.cornerRadius = [[viewBehavior getCornerRadius] intValue];
        space.layer.masksToBounds = YES;
    }
    
    if ([viewBehavior getBorderSize]) {
        space.layer.borderWidth = [[viewBehavior getBorderSize] floatValue];
        space.layer.borderColor = [[FFView color:[viewBehavior getBorderColor]] CGColor];
    }
    
    return space;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFSpace *space = fluidView;
    space.frame = bounds;
    
    if (![view isVisible]) {
        space.hidden = YES;
        return;
    } else {
        space.hidden = NO;
    }
}

- (void)cleanupFluidViewWithId:(id)fluidView {
}

@end

@implementation TableBuilder

- (id)init {
    if (self == [super init]) {
    }
    return self;
}

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) view->viewBehavior_;
    
    UITableViewStyle style = UITableViewStyleGrouped;
    if ([viewBehavior isStickyHeaders]) {
        style = UITableViewStylePlain;
    }
    
    FFTableView *table = [[FFTableView alloc] initWithFrame:bounds style:style];
    
    if ([viewBehavior getPaddingBottom]) {
        float paddingBottom = 0;
        paddingBottom = [viewBehavior getPaddingBottom].floatValue;
        table.tableFooterView = [[UIView alloc] initWithFrame:CGRectMake(0,0,10,paddingBottom)];
        table.tableFooterView.backgroundColor = [UIColor clearColor];
    } else {
        table.tableFooterView = [[UIView alloc] init]; // Removes empty rows with separates from bottom of table
    }
    
    if ([[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
        table.backgroundColor = [UIColor clearColor];
        [table setBackgroundView:nil];
        table.contentInset = UIEdgeInsetsMake(0, -10, 0, 0);
    }
    
    table.viewPath = info.viewPath;
    table.dataModelListenerId = (info.fluidView.listenToDataModelChanges) ? info.listenerId : nil;
    table.fluidType = view->viewBehavior_->type_;

    NSString *tableLayoutId = [viewBehavior getTableLayoutId];

    table.sectionHeaderHeight = 0;
    table.sectionFooterHeight = 0;
    
    table.tableHeaderView = [[UIView alloc] initWithFrame:CGRectMake(0,0,.01,.001)];
    
    // HSTDBC tables not getting deallocated in modal views
    
    FFTableViewDelegate *delegate = [[FFTableViewDelegate alloc] initWithView:view];
    table.dataSource = delegate;
    table.delegate = delegate; // not retained
    table.fluidTableDelegate = delegate;
    
    if ([viewBehavior isShowRowDivider]) {
        table.separatorStyle = UITableViewCellSeparatorStyleSingleLine;
    } else {
        table.separatorStyle = UITableViewCellSeparatorStyleNone;
    }
    
    if ([viewBehavior isScrollToBottomOnLoad]) {//  && table.contentSize.height > table.frame.size.height) {
        //CGPoint offset = CGPointMake(0, table.contentSize.height - table.frame.size.height);
        //[table setContentOffset:offset animated:NO];
        [table setContentOffset:CGPointMake(0, CGFLOAT_MAX)];
    }
    
    BOOL scrollToBottomOnLoad = [viewBehavior isScrollToBottomOnLoad];
    BOOL scrollToTopOnUpdate = [viewBehavior isScrollToTopWhenUpdate];
    
    
    __weak typeof(table) weakRef = table;
    __weak typeof(tableLayoutId) weakLayoutId = tableLayoutId;
    //__unsafe_unretained FFTableView *weakRef = table;
    
    if (info.fluidView.listenToDataModelChanges) {
        
        observerBlock block = ^(NSString *key, NSArray *subkeys) {
            if ([key isEqualToString:view->key_]) {
                [weakRef reloadData];
                if (scrollToBottomOnLoad) {
                    dispatch_async(dispatch_get_global_queue(0, 0), ^{
                        dispatch_async(dispatch_get_main_queue(), ^{
                            if (weakRef.contentSize.height > weakRef.frame.size.height) {
                                CGPoint offset = CGPointMake(0, weakRef.contentSize.height - weakRef.frame.size.height);
                                [weakRef setContentOffset:offset animated:NO];
                            }
                        });
                    });
                }
                
                if (scrollToTopOnUpdate) {
                    [weakRef scrollRectToVisible:CGRectMake(0, 0, 1, 1) animated:YES];
                }
            } else {
                
                if (weakLayoutId) {
                    // If this is a table-layout, then reload the whole table
                    [weakRef reloadData];
                    return;
                }
                
                // reload just the row
                NSArray *comps = [key componentsSeparatedByString:@"."];
                long long int objectId = [[comps lastObject] longLongValue];
                
                int rowIndex = [weakRef rowIndexOfObjectWithId:objectId];
                
                NSIndexPath* rowToReload = [NSIndexPath indexPathForRow:rowIndex inSection:0];
                NSArray *array = [NSArray arrayWithObjects:rowToReload, nil];
                //[weakRef reloadRowsAtIndexPaths:array withRowAnimation:UITableViewRowAnimationNone];
                
                dispatch_async(dispatch_get_global_queue(0, 0), ^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [weakRef beginUpdates];
                        [weakRef reloadRowsAtIndexPaths:array withRowAnimation:UITableViewRowAnimationNone];
                        [weakRef endUpdates];
                    });
                });

            }
        };
        
        observerBlockDataRemoved blockRemoved = ^(NSString *key) {
            
            NSArray *comps = [key componentsSeparatedByString:@"."];
            long long int objectId = [[comps lastObject] longLongValue];
            
            int rowIndex = [weakRef rowIndexOfDeletedObjectWithId:objectId];
            
            NSIndexPath* rowToDelete = [NSIndexPath indexPathForRow:rowIndex inSection:0];
            NSMutableArray *array = [NSMutableArray array];
            [array addObject:rowToDelete];
            
            dispatch_async(dispatch_get_global_queue(0, 0), ^{
                dispatch_async(dispatch_get_main_queue(), ^{
                    [weakRef beginUpdates];
                    [weakRef deleteRowsAtIndexPaths:array withRowAnimation:UITableViewRowAnimationRight];
                    [weakRef endUpdates];
                });
            });
            
        };
        
        NSString *key = view->key_;
        
        if ([viewBehavior getTableLayoutId]) {
            key = [viewBehavior getTableLayoutId];
        }
        
        [FFViewFactoryRegistration addDataChangeObserverFor:info.dataModelKeyPrefix
                                               dataModelKey:key
                                                 listenerId:table.dataModelListenerId
                                          listenForChildren:YES
                                                      block:block
                                           blockDataRemoved:blockRemoved];
    }
    
    return table;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorTable *viewBehavior = (FFTViewBehaviorTable *) view->viewBehavior_;
    
    UITableView *table = fluidView;
    
    if (![view isVisible]) {
        table.hidden = YES;
        return;
    } else {
        table.hidden = NO;
    }
    
    if (!info.fromDataListener) {
        [table reloadData];
    }
    
    if ([viewBehavior getScrollEnabled]) {
        table.scrollEnabled = [[viewBehavior getScrollEnabled] booleanValue];
    }

    if ([viewBehavior getShowsVerticalScrollIndicator]) {
        table.showsVerticalScrollIndicator = [[viewBehavior getShowsVerticalScrollIndicator] booleanValue];
    }
    
    if ([viewBehavior getBackgroundColorWithNSString:info.dataModelKeyPrefix]) {
        table.backgroundColor = [FFView color:[viewBehavior getBackgroundColorWithNSString:info.dataModelKeyPrefix]];
    } else {
        table.backgroundColor = [UIColor clearColor];
    }
    
    table.frame = bounds;
}

- (void)cleanupFluidViewWithId:(id)fluidView {
    FFTableView *table = fluidView;
    if (table.dataModelListenerId) {
        [FFViewFactoryRegistration removeDataChangeObserverFor:table.dataModelListenerId];
    }
    
    [table.fluidTableDelegate cleanup];
    
    [table cleanup];
    
    table.dataSource = nil;
    table.delegate = nil;
    table.fluidTableDelegate = nil;
}

@end

@implementation ImageBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFImageView *image = [[FFImageView alloc] initWithFrame:bounds];
    image.viewPath = info.viewPath;
    image.dataModelKeyParent = info.dataModelKeyPrefix;
    image.dataModelKey = [view getKey];
    
    return image;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    FFTViewBehaviorImage *viewBehavior = (FFTViewBehaviorImage *) view->viewBehavior_;
    
    FFImageView *image = fluidView;
    
    if (![view isVisible]) {
        image.hidden = YES;
        return;
    } else {
        image.hidden = NO;
    }
    
    NSString *imageNameKey = [viewBehavior getImageWithWithNSString:info.dataModelKeyPrefix];

    FFTViewBehaviorImage_ImageBounds *imageBounds = [viewBehavior getImageBoundsWithNSString:imageNameKey withDouble:bounds.size.width withDouble:bounds.size.height];

    NSString *imageName = nil;
    if (imageNameKey) {
        imageName = [[[FFTGlobalState fluidApp] getImageManager] getImageNameWithNSString:imageNameKey withInt:[imageBounds getWidth] withInt:[imageBounds getHeight]];
    }
    
    if (imageName) {
        if (![image.imageName isEqualToString:imageName]) {
            UIImage *uiImage;
            if ([viewBehavior getTintColor] || [viewBehavior getTintColorKey]) {
                uiImage = [[UIImage imageNamed:imageName] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
                if ([viewBehavior getTintColor]) {
                    image.tintColor = [FFView color:[viewBehavior getTintColor]];
                } else {
                    NSString *colorString = [[[FFTGlobalState fluidApp] getDataModelManager] getValueWithNSString:info.dataModelKeyPrefix withNSString:[viewBehavior getTintColorKey] withNSString:@"{0}" withNSString:nil];
                    FFTColor *color = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorString];
                    image.tintColor = [FFView color:color];
                }
            } else {
                uiImage = [UIImage imageNamed:imageName];
                image.tintColor = nil;
            }
            [image setImage:uiImage];
            image.imageBounds = imageBounds;
            image.imageName = imageName;
        }
        image.hidden = NO;
    } else {
        [image setImage:nil];
        image.imageName = nil;
        image.hidden = YES;
    }
    
    [image setFrame:bounds];    
}

- (void)cleanupFluidViewWithId:(id)fluidView {
}

@end

@implementation SubviewBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorSubview *viewBehavior = (FFTViewBehaviorSubview *) view->viewBehavior_;
    
    FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:[viewBehavior getSubview]];
    
    if (layout == nil) {
        [NSException raise:@"Subview is null" format:@"Subview is null %@", [viewBehavior getSubview]];
        return nil;
    }
    
    NSString *dataModelPrefix = info.dataModelKeyPrefix;
    /*
    NSString *subviewDataModelPrefix;
    if (dataModelPrefix == nil || [dataModelPrefix isEqualToString:@""]) {
        subviewDataModelPrefix = [NSString stringWithFormat:@"%@.", [viewBehavior getKey]];
    } else {
        subviewDataModelPrefix = [NSString stringWithFormat:@"%@.%@.", dataModelPrefix, [viewBehavior getKey]];
    }*/
    
    FFView *subview = [[FFView alloc] initWithFrame:bounds viewPath:info.viewPath layout:layout rootFFView:info.fluidView inTableView:info.tableView];
    subview.dataModelKeyPrefix = dataModelPrefix;
    return subview;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;

    FFView *subview = fluidView;
    
    subview.frame = bounds;
    
    if (![view isVisible]) {
        subview.hidden = YES;
        return;
    } else {
        subview.hidden = NO;
    }
    
    [subview createOrUpdateViews:bounds fromDataListener:NO];
}

- (void)cleanupFluidViewWithId:(id)fluidView {
    
    FFView *subview = fluidView;
    [subview cleanup];
}

@end

@implementation SubviewRepeatBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                          withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorSubviewRepeat *viewBehavior = (FFTViewBehaviorSubviewRepeat *) view->viewBehavior_;
    
    FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:[viewBehavior getSubview]];
    
    if (layout == nil) {
        [NSException raise:@"Subview is null" format:@"Subview is null %@", [viewBehavior getSubview]];
        return nil;
    }
    
    FFSubviewRepeat *subviewRepeat = [[FFSubviewRepeat alloc] initWithFrame:bounds];
    subviewRepeat.fluidView = info.fluidView;
    
    NSString *dataModelPrefix = info.dataModelKeyPrefix;
    NSString *changeListenerKey;
    NSString *subviewDataModelPrefix;
    if (dataModelPrefix == nil || [dataModelPrefix isEqualToString:@""]) {
        changeListenerKey = [viewBehavior getKey];
        subviewDataModelPrefix = [NSString stringWithFormat:@"%@.", [viewBehavior getKey]];
    } else {
        changeListenerKey = [NSString stringWithFormat:@"%@.%@", dataModelPrefix, [viewBehavior getKey]];
        subviewDataModelPrefix = [NSString stringWithFormat:@"%@.%@.", dataModelPrefix, [viewBehavior getKey]];
    }
    int size = [[[[FFTGlobalState fluidApp] getDataModelManager] getValueListWithNSString:dataModelPrefix withNSString:[viewBehavior getKey]] size];

    for (int index = 0; index < size; index++) {
        NSString *viewPath = [NSString stringWithFormat:@"%@.%d", info.viewPath, index];
        FFView *subview = [[FFView alloc] initWithFrame:bounds viewPath:viewPath layout:layout rootFFView:info.fluidView inTableView:info.tableView];
        subview.lastViewPathTokenIsIndex = YES;
        subview.dataModelKeyPrefix = [NSString stringWithFormat:@"%@%d", subviewDataModelPrefix, index];
        [subviewRepeat addSubview:subview];
    }
    
    NSString *listener = [NSString stringWithFormat:@"_subviewrepeat-%@-%@", info.viewPath, changeListenerKey];
    subviewRepeat.listener = listener;

    __weak typeof(subviewRepeat) weakSubviewRepeat = subviewRepeat;
    [FFViewFactoryRegistration addDataChangeObserverFor:nil dataModelKey:changeListenerKey listenerId:listener
                                      listenForChildren:NO
                                                  block:^(NSString *key, NSArray *subkeys) {
                                                      NSMutableArray* array = [NSMutableArray array];
                                                      for (UIView *subview in [weakSubviewRepeat subviews]) {
                                                          [((FFView *) subview) cleanup];
                                                          [array addObject:subview];
                                                      }
                                                      for (UIView *subview in array) {
                                                          [subview removeFromSuperview];
                                                      }
                                                      [self updateFluidViewWithId:weakSubviewRepeat withFFTViewPosition:view withId:info];
                                                      [weakSubviewRepeat.fluidView setScrollViewContentSize];
                                                  }
                                       blockDataRemoved:nil];

    [[[FFTGlobalState fluidApp] getDataModelManager] addDataChangeListenerWithNSString:changeListenerKey withNSString:listener withFFTDataChangeListener:info.fluidView];
    
    return subviewRepeat;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorSubviewRepeat *viewBehavior = (FFTViewBehaviorSubviewRepeat *) view->viewBehavior_;
    NSString *dataModelPrefix = info.dataModelKeyPrefix;

    NSString *subviewDataModelPrefix;
    if (dataModelPrefix == nil || [dataModelPrefix isEqualToString:@""]) {
        subviewDataModelPrefix = [NSString stringWithFormat:@"%@.", [viewBehavior getKey]];
    } else {
        subviewDataModelPrefix = [NSString stringWithFormat:@"%@.%@.", dataModelPrefix, [viewBehavior getKey]];
    }

    FFSubviewRepeat *subviewRepeat = fluidView;
    
    if (![view isVisible]) {
        subviewRepeat.hidden = YES;
        return;
    } else {
        subviewRepeat.hidden = NO;
    }
    
    BOOL subviewCreated = NO;
    
    subviewRepeat.frame = bounds;
    
    FFTLayout *layout = [[FFTGlobalState fluidApp] getLayoutWithNSString:[viewBehavior getSubview]];
    
    id<JavaUtilList> valueList = [[[FFTGlobalState fluidApp] getDataModelManager] getValueListWithNSString:dataModelPrefix withNSString:[viewBehavior getKey]];
    if (valueList != nil) {
        int size = [valueList size];
        double height = 0;
        for (int index = 0; index < size; index++) {
            
            NSString *viewPath = [NSString stringWithFormat:@"%@.%d", info.viewPath, index];
            
            FFView *subview;
            if (index >= [[subviewRepeat subviews] count]) {
                
                subview = [[FFView alloc] initWithFrame:bounds viewPath:viewPath layout:layout rootFFView:info.fluidView inTableView:info.tableView];
                subview.lastViewPathTokenIsIndex = YES;
                subview.dataModelKeyPrefix = [NSString stringWithFormat:@"%@%d", subviewDataModelPrefix, index];
                [subviewRepeat addSubview:subview];
                subviewCreated = YES;
            } else {
            
                subview = [subviewRepeat subviews][index];
            }
            
            FFTLayout *l = [[FFTGlobalState fluidApp] getLayoutWithNSString:[viewBehavior getSubview]];

            BOOL landscape = [[FFTGlobalState_fluidApp__ getUiService] isOrientationLandscape];

            double calcHeight = -1;
            if (info.precompute) {
                
                FFTViewPosition *viewPosition = [[[FFTGlobalState fluidApp] getPrecomputeLayoutManager] getViewPositionWithNSString:viewPath];
                if (viewPosition) {
                    calcHeight = [viewPosition getHeight];
                }
            }
            if (calcHeight == -1) {
                
                calcHeight = [l calculateHeightWithBoolean:landscape withFloat:bounds.size.width withNSString:[NSString stringWithFormat:@"%@%d", subviewDataModelPrefix, index] withBoolean:NO];
            }
            
            CGRect boundsForSubview = CGRectMake(0, height, bounds.size.width, calcHeight);
            [subview createOrUpdateViews:boundsForSubview fromDataListener:NO];
            height += calcHeight;
        }
    }
    
    if (subviewCreated && info.tableView) {
        //[info.tableView beginUpdates];
        //[info.tableView endUpdates];
        [info.tableView reloadData];
    }
    
}

- (void)cleanupFluidViewWithId:(id)fluidView {
    
    FFSubviewRepeat *subviewRepeat = fluidView;
    
    FFView *subview;
    for (int index = 0; index < [[subviewRepeat subviews] count]; index++) {
        subview = [subviewRepeat subviews][index];
        [subview cleanup];
    }
    
    [[[FFTGlobalState fluidApp] getDataModelManager] removeDataChangeListenerWithNSString:subviewRepeat.listener];
}

@end

@implementation WebViewBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;

    FFWebView *webView = [[FFWebView alloc] initWithFrame:bounds];
    webView.viewPath = info.viewPath;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        FFTViewBehaviorWebView *viewBehavior= (FFTViewBehaviorWebView *) view->viewBehavior_;
        
        NSString *html = [viewBehavior getHtml];
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            [webView loadHTMLString:html baseURL:[NSURL fileURLWithPath:[[NSBundle mainBundle] bundlePath]]];
        });
    });
    
    return webView;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    UIWebView *webView = fluidView;
    
    if (![view isVisible]) {
        webView.hidden = YES;
        return;
    } else {
        webView.hidden = NO;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            
            webView.frame = bounds;
            NSString *jsCall = [NSString stringWithFormat:@"resizeLayout(%f,%f)", bounds.size.width, bounds.size.height];
            [webView stringByEvaluatingJavaScriptFromString:jsCall];
            jsCall = [NSString stringWithFormat:@"fluidViewWasUpdated(%f,%f)", bounds.size.width, bounds.size.height];
            [webView stringByEvaluatingJavaScriptFromString:jsCall];
        });
    });
}

- (void)cleanupFluidViewWithId:(id)fluidView {
}

@end

@implementation URLWebViewBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                  withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    UIWebView *webView = [[UIWebView alloc] initWithFrame:bounds];

    [webView setBackgroundColor:[UIColor clearColor]];
    webView.opaque = NO; // Removes black bar, webview is transparent until page is loaded
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        FFTViewBehaviorURLWebView *viewBehavior= (FFTViewBehaviorURLWebView *) view->viewBehavior_;
        
        NSString *url = [viewBehavior getUrl];
        NSString *urlKey = [viewBehavior getUrlKey];
        if (urlKey) {
            url = [view getValueWithNSString:info.dataModelKeyPrefix withNSString:urlKey withNSString:url];
        }
        
        dispatch_sync(dispatch_get_main_queue(), ^{
            NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
            [webView loadRequest:req];
        });
    });
    
    return webView;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    UIWebView *webView = fluidView;
    
    if (![view isVisible]) {
        webView.hidden = YES;
        return;
    } else {
        webView.hidden = NO;
    }
    
    webView.frame = bounds;
}

- (void)cleanupFluidViewWithId:(id)fluidView {
}

@end

@interface TextfieldBuilder ()
@property (nonatomic, assign) UITextField *currentTextField;
@end
@implementation TextfieldBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                                             withId:(id)userInfo {
    
    FFTViewBehaviorTextfield *viewBehavior = (FFTViewBehaviorTextfield *) view->viewBehavior_;
    
    if ([viewBehavior isMultiLine]) {
        return [self createMulti:view withId:userInfo];
    }
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTextField *textfield = [[FFTextField alloc] initWithFrame:bounds view:view];
    textfield.dataModelListenerId = (info.fluidView.listenToDataModelChanges) ? info.listenerId : nil;
    textfield.delegate = info.fluidView;
    textfield.adjustsFontSizeToFitWidth = YES;
    textfield.dataModelKeyPrefix = info.dataModelKeyPrefix;
    textfield.viewPath = info.viewPath;
    textfield.viewBehavior = viewBehavior;
    
    
    if ([viewBehavior getFormattedPlaceholder]) {
        FFTAttributedText *attText = [[FFTAttributedText alloc] initWithNSString:[viewBehavior getFormattedPlaceholder]];
        NSAttributedString *attString = [FFViewFactoryRegistration createAttributedString:attText size:12 defaultColor:[UIColor darkTextColor] fontName:Nil];
        textfield.attributedPlaceholder = attString;
    } else {
        textfield.placeholder = [viewBehavior getLabel];        
    }
    
    NSString *keyboard = [viewBehavior getKeyboard];
    if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardEmail_]) {
        textfield.keyboardType = UIKeyboardTypeEmailAddress;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardNumber_]) {
        textfield.keyboardType = UIKeyboardTypeNumberPad;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardPhone_]) {
        textfield.keyboardType = UIKeyboardTypePhonePad;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardUrl_]) {
        textfield.keyboardType = UIKeyboardTypeURL;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardAlphabet_]) {
        textfield.keyboardType = UIKeyboardTypeAlphabet;
    } else {
        textfield.keyboardType = UIKeyboardTypeDefault;
    }
    
    if (![viewBehavior isAutoCorrect] || [viewBehavior isPassword] || [keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardEmail_]) {
        textfield.autocorrectionType = UITextAutocorrectionTypeNo;
        textfield.autocapitalizationType = UITextAutocapitalizationTypeNone;
    }
    
    NSString *capitalize = [viewBehavior getCapitalize];
    if ([capitalize isEqualToString:@"words"]) {
        textfield.autocapitalizationType = UITextAutocapitalizationTypeWords;
    } else if ([capitalize isEqualToString:@"none"]) {
        textfield.autocapitalizationType = UITextAutocapitalizationTypeNone;
    }
    
    NSString *borderStyle = [viewBehavior getBorderStyle];
    if ([borderStyle isEqualToString:FFTViewBehaviorTextfield_kBorderStyleNone_]) {
        textfield.borderStyle = UITextBorderStyleNone;
    } else {
        textfield.borderStyle = UITextBorderStyleRoundedRect;
    }
    
    if ([viewBehavior isPassword]) {
        textfield.secureTextEntry = YES;
    }
    
    if ([viewBehavior getCornerRadius]) {
        textfield.layer.cornerRadius = [viewBehavior getCornerRadius].intValue;
    }
    
    if ([viewBehavior getBorderSize]) {
        textfield.layer.borderWidth = [[viewBehavior getBorderSize] floatValue];
    }
    
    if ([viewBehavior getBorderColor]) {
        textfield.layer.borderColor = [[FFView color:[viewBehavior getBorderColor]] CGColor];
    }
    
    // hstdbc adjust font size for fit
    
    [textfield addTarget:self action:@selector(editingChanged:) forControlEvents:UIControlEventEditingChanged];
    
    __weak typeof(textfield) weakTf = textfield;
    __weak typeof(viewBehavior) weakViewBehavior = viewBehavior;
    [info.fluidView addTappedOutsideWhileFocusedListener:textfield tappedOutsideWhileFocusedListener:^{
        if ([weakViewBehavior isDismissKeyboardWithTap]) {
            [weakTf resignFirstResponder];
        }
    }];
    
    if (info.fluidView.listenToDataModelChanges) {
        [FFViewFactoryRegistration addDataChangeObserverFor:info.dataModelKeyPrefix dataModelKey:view->key_ listenerId:textfield.dataModelListenerId
                                          listenForChildren:NO
                                                      block:^(NSString *key, NSArray *subkeys) {
                                                          weakTf.text = [FFView valueFor:view baseText:@"{0}" dataModelKeyPrefix:info.dataModelKeyPrefix];
                                                      }
                                           blockDataRemoved:nil];
    }
    
    return textfield;
}

- (id)createMulti:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorTextfield *viewBehavior = (FFTViewBehaviorTextfield *) view->viewBehavior_;
    
    FFTextView *textfield = [[FFTextView alloc] initWithFrame:bounds view:view];
    textfield.fluidView = info.fluidView;
    textfield.dataModelListenerId = (info.fluidView.listenToDataModelChanges) ? info.listenerId : nil;
    textfield.dataModelKeyPrefix = info.dataModelKeyPrefix;
    textfield.viewPath = info.viewPath;
    textfield.viewBehavior = viewBehavior;
    if ([viewBehavior getPlaceholderTextColor]) {
        textfield.placeholderColor = [FFView color:[viewBehavior getPlaceholderTextColor]];
    }
    
    if ([viewBehavior getBackgroundColorWithNSString:info.dataModelKeyPrefix]) {
        textfield.backgroundColor = [FFView color:[viewBehavior getBackgroundColorWithNSString:info.dataModelKeyPrefix]];
    } else {
        textfield.backgroundColor = [UIColor clearColor];
    }
    
    NSString *keyboard = [viewBehavior getKeyboard];
    if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardEmail_]) {
        textfield.keyboardType = UIKeyboardTypeEmailAddress;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardNumber_]) {
        textfield.keyboardType = UIKeyboardTypeNumberPad;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardPhone_]) {
        textfield.keyboardType = UIKeyboardTypePhonePad;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardUrl_]) {
        textfield.keyboardType = UIKeyboardTypeURL;
    } else if ([keyboard isEqualToString:FFTViewBehaviorTextfield_kKeyboardAlphabet_]) {
        textfield.keyboardType = UIKeyboardTypeAlphabet;
    } else {
        textfield.keyboardType = UIKeyboardTypeDefault;
    }

    if (![viewBehavior isAutoCorrect]) {
        textfield.autocorrectionType = UITextAutocorrectionTypeNo;
    }
    
    if ([[viewBehavior getBorderSize] floatValue] > 0) {
        textfield.layer.borderWidth = [[viewBehavior getBorderSize] floatValue];
        textfield.layer.borderColor = [[FFView color:[viewBehavior getBorderColor]] CGColor];
    }
    
    // hstdbc [textfield addTarget:self action:@selector(editingChanged:) forControlEvents:UIControlEventEditingChanged];
    //[textfield addTarget:self action:@selector(editingChanged:) forControlEvents:UIControlEventEditingChanged];
    
    textfield.delegate = self;
    
    textfield.placeholderText = [viewBehavior getLabel];
    textfield.text = textfield.placeholderText;
    textfield.showingPlaceholder = YES;
    if (textfield.placeholderColor) {
        textfield.textColor = textfield.placeholderColor;
    }

    __weak typeof(textfield) weakTf = textfield;
    __weak typeof(viewBehavior) weakViewBehavior = viewBehavior;
    [info.fluidView addTappedOutsideWhileFocusedListener:textfield tappedOutsideWhileFocusedListener:^{
        if ([weakViewBehavior isDismissKeyboardWithTap]) {
            [weakTf resignFirstResponder];
        }
    }];
    
    if (info.fluidView.listenToDataModelChanges) {
        [FFViewFactoryRegistration addDataChangeObserverFor:info.dataModelKeyPrefix dataModelKey:view->key_ listenerId:textfield.dataModelListenerId
                                          listenForChildren:NO
                                                      block:^(NSString *key, NSArray *subkeys) {
                                                          weakTf.text = [FFView valueFor:view baseText:@"{0}" dataModelKeyPrefix:info.dataModelKeyPrefix];
                                                      }
                                           blockDataRemoved:nil];
    }
    
    return textfield;
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBehaviorTextfield *viewBehavior = (FFTViewBehaviorTextfield *) view->viewBehavior_;
    
    if ([viewBehavior isMultiLine]) {
        [self updateMulti:fluidView withFFTViewPosition:view withId:userInfo];
        return;
    }
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTextField *textfield = fluidView;
    textfield.frame = bounds;
    
    textfield.enabled = [viewBehavior isEnabledWithNSString:info.dataModelKeyPrefix];
    if (textfield.enabled) {
        UIColor* textColor = [UIColor blackColor];
        if ([viewBehavior getTextEnabledColor]) {
            textColor = [FFView color:[viewBehavior getTextEnabledColor]];
        }
        
        [textfield setTextColor:textColor];
    } else {
        UIColor* textColor = [UIColor blackColor];
        if ([viewBehavior getTextDisabledColor]) {
            textColor = [FFView color:[viewBehavior getTextDisabledColor]];
        }
        
        [textfield setTextColor:textColor];
    }
    
    
    if (![view isVisible]) {
        textfield.hidden = YES;
        return;
    } else {
        textfield.hidden = NO;
    }
    
    if (view->key_) {
        textfield.text = [FFView valueFor:view baseText:nil dataModelKeyPrefix:info.dataModelKeyPrefix];
    }
}

- (void)updateMulti:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBehaviorTextfield *viewBehavior = (FFTViewBehaviorTextfield *) view->viewBehavior_;

    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTextView *textfield = fluidView;
    textfield.frame = bounds;
    
    textfield.editable = [viewBehavior isEnabledWithNSString:info.dataModelKeyPrefix];
    
    if (view->key_) {
        textfield.text = [FFView valueFor:view baseText:nil dataModelKeyPrefix:info.dataModelKeyPrefix];
    }
    
    if (!textfield.currentlyEditing &&
            textfield.placeholderText &&
            (textfield.text == nil || [textfield.text isEqualToString:@""])) {
        
        textfield.text = textfield.placeholderText;
        textfield.showingPlaceholder = YES;
    } else {
        textfield.showingPlaceholder = NO;
    }

    if (textfield.showingPlaceholder && textfield.placeholderColor) {
        textfield.textColor = textfield.placeholderColor;
    } else {
        textfield.textColor = nil;
    }
    
    UITextField *field = [[UITextField alloc] init];
    float defaultPointSize = field.font.pointSize;
    
    [textfield setFont:[UIFont systemFontOfSize:defaultPointSize]];
}

- (void)editingChanged:(FFTextField *)field {
    [[[FFTGlobalState fluidApp] getEventsManager] userChangedValueToWithNSString:field.viewPath withFFTActionListener_EventInfo:nil withId:field.text];
    [FFView setValueFor:field.view dataModelKeyPrefix:field.dataModelKeyPrefix to:field.text];
}

- (void)cleanupFluidViewWithId:(id)fluidView {
    FFTextField *view = fluidView;
    if (view.dataModelListenerId) {
        [FFViewFactoryRegistration removeDataChangeObserverFor:view.dataModelListenerId];
    }
}

- (BOOL)textViewShouldBeginEditing:(UITextView *)textView {
    FFTextView *textfield = (FFTextView *) textView;
    textfield.currentlyEditing = YES;
    textfield.textColor = nil;
    if (textfield.showingPlaceholder) {
        textfield.text = nil;
        textfield.showingPlaceholder = NO;
    }
    return YES;
}

- (BOOL)textViewShouldEndEditing:(UITextView *)textView {
    FFTextView *textfield = (FFTextView *) textView;
    textfield.currentlyEditing = NO;
    if (textfield.text == nil || [textfield.text isEqualToString:@""]) {
        if (textfield.placeholderText) {
            textfield.text = textfield.placeholderText;
            textfield.showingPlaceholder = YES;
        }
    }
    if (textfield.showingPlaceholder && textfield.placeholderColor) {
        textfield.textColor = textfield.placeholderColor;
    }
    return YES;
}

- (void)textViewDidBeginEditing:(UITextView *)textView {
    FFTextView *textfield = (FFTextView *) textView;
    [textfield.fluidView textFieldDidBeginEditingHelper:textView];
}

- (void)textViewDidEndEditing:(UITextView *)textView {
    FFTextView *textfield = (FFTextView *) textView;
    [textfield.fluidView textFieldDidEndEditingHelper:textView];
}

- (void)textViewDidChange:(UITextView *)textView {
    FFTextView *field = (FFTextView *) textView;
    [[[FFTGlobalState fluidApp] getEventsManager] userChangedValueToWithNSString:field.viewPath withFFTActionListener_EventInfo:nil withId:field.text];
    [FFView setValueFor:field.view dataModelKeyPrefix:field.dataModelKeyPrefix to:field.text];
}

@end

@implementation SearchbarBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                          withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    CGRect frame = CGRectMake(0, 0, bounds.size.width, bounds.size.height);

    FFSearchBar *searchBar = [[FFSearchBar alloc] initWithFrame:frame viewPath:info.viewPath fluidView:view];
    searchBar.dataModelKeyParent = info.dataModelKeyPrefix;
    searchBar.dataModelKey = [view getKey];
    
    FFTViewBehaviorSearchbar *viewBehavior = (FFTViewBehaviorSearchbar *) view->viewBehavior_;

    if ([viewBehavior getPlaceholderText]) {
        searchBar.placeholder = [viewBehavior getPlaceholderText];
    }

    searchBar.showsCancelButton = [viewBehavior isShowCancelButton];
    
    searchBar.delegate = self;
    
    searchBar.backgroundImage = [[UIImage alloc] init];
    searchBar.backgroundColor = [FFView color:[viewBehavior getSearchBarBackgroundColor]];
    
    return searchBar;
}

- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText {
    
    FFSearchBar *ffbar = (FFSearchBar *)searchBar;
    
    [[[FFTGlobalState fluidApp] getEventsManager] userChangedValueToWithNSString:ffbar.viewPath withFFTActionListener_EventInfo:nil withId:searchText];

    //[ffbar.fluidView setUserValueWithId:searchText];
    
    FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
    [eventInfo setDataModelKeyParentWithNSString:ffbar.dataModelKeyParent];
    [eventInfo setDataModelKeyWithNSString:ffbar.dataModelKey];
    [[[FFTGlobalState fluidApp] getEventsManager] userChangedValueToWithNSString:ffbar.viewPath withFFTActionListener_EventInfo:eventInfo withId:searchText];
}

- (void)searchBarCancelButtonClicked:(UISearchBar *)searchBar {

    FFSearchBar *ffbar = (FFSearchBar *)searchBar;
    
    [[[FFTGlobalState fluidApp] getEventsManager] userCancelledWithNSString:ffbar.viewPath];
}

- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorSearchbar *viewBehavior = (FFTViewBehaviorSearchbar *) view->viewBehavior_;
    
    UISearchBar *searchBar = fluidView;
    searchBar.frame = bounds;
    
    if (![view isVisible]) {
        searchBar.hidden = YES;
        return;
    } else {
        searchBar.hidden = NO;
    }
}

- (void)cleanupFluidViewWithId:(id)fluidView {
}

@end

@implementation SegmentedControlBuilder

- (id)createFluidViewWithFFTViewPosition:(FFTViewPosition *)view
                          withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFTViewBehaviorSegmentedControl *viewBehavior = (FFTViewBehaviorSegmentedControl *) view->viewBehavior_;
    
    CGRect frame = CGRectMake(0, 0, bounds.size.width, bounds.size.height);
    
    NSMutableArray *options = [NSMutableArray array];
    for (NSString *option in [viewBehavior getOptions]) {
        [options addObject:option];
    }
    
    FFSegmentedControl *control = [[FFSegmentedControl alloc] initWithItems:options];
    control.frame = frame;
    control.viewPath = info.viewPath;
    control.selectedSegmentIndex = 0;
    
    [control addTarget:self action:@selector(segControlAction:) forControlEvents:UIControlEventValueChanged];
        
    if ([viewBehavior getLineColor]) {
        UIColor *color = [FFView color:[viewBehavior getLineColor]];
        control.tintColor = color;
    }
    
    if ([viewBehavior getTextColor]) {
        UIColor *color = [FFView color:[viewBehavior getTextColor]];
        [control setTitleTextAttributes:@{NSForegroundColorAttributeName:color} forState:UIControlStateNormal];
    }
    
    if ([viewBehavior getSelectedTextColor]) {
        UIColor *color = [FFView color:[viewBehavior getSelectedTextColor]];
        [control setTitleTextAttributes:@{NSForegroundColorAttributeName:color} forState:UIControlStateSelected];
    }
    
    return control;
}

- (void)segControlAction:(UISegmentedControl *)segmentedControl {

    JavaLangInteger *i = [[JavaLangInteger alloc] initWithInt:segmentedControl.selectedSegmentIndex];
    
    FFSegmentedControl *control = (FFSegmentedControl *) segmentedControl;

    FFTActionListener_EventInfo *eventInfo = [[FFTActionListener_EventInfo alloc] init];
    [eventInfo setUserInfoWithId:i];
    [[[FFTGlobalState fluidApp] getEventsManager] userTappedWithNSString:control.viewPath withFFTActionListener_EventInfo:eventInfo];
}


- (void)updateFluidViewWithId:(id)fluidView withFFTViewPosition:(FFTViewPosition *)view withId:(id)userInfo {
    
    FFTViewBuilderInfo *info = userInfo;
    CGRect bounds = info.bounds;
    
    FFSegmentedControl *control = fluidView;
    control.frame = bounds;
    
    FFTViewBehaviorSegmentedControl *viewBehavior = (FFTViewBehaviorSegmentedControl *) view->viewBehavior_;
    if ([viewBehavior getSelectedIndexKey]) {
        
        NSString *index = [[[FFTGlobalState fluidApp] getDataModelManager] getValueWithNSString:nil withNSString:[viewBehavior getSelectedIndexKey] withNSString:@"{0}" withNSString:nil];
        control.selectedSegmentIndex = [index intValue];
    }
    
    if (![view isVisible]) {
        control.hidden = YES;
        return;
    } else {
        control.hidden = NO;
    }
}

- (void)cleanupFluidViewWithId:(id)fluidView {
}

@end