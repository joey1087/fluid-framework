//
//  FFTextView.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 12/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTViewPosition, FFView, FFTViewBehaviorTextfield;

@interface FFTextView : UITextView

@property (nonatomic, weak) FFTViewPosition *view;
@property (nonatomic, assign) NSString *dataModelListenerId;
@property (nonatomic, assign) NSString *dataModelKeyPrefix;
@property (nonatomic, strong) NSString *placeholderText;
@property (nonatomic, assign) BOOL showingPlaceholder;
@property (nonatomic, assign) BOOL currentlyEditing;
@property (nonatomic, assign) FFView *fluidView;
@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) UIColor *placeholderColor;
@property (nonatomic, strong) FFTViewBehaviorTextfield *viewBehavior;

- (id)initWithFrame:(CGRect)frame view:(FFTViewPosition *)view;

@end
