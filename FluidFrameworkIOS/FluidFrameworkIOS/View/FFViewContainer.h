//
//  FFViewContainer.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 8/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFView.h"

@class FFTViewBehaviorLabel;

@interface FFViewContainer : UIView

@property (nonatomic, strong) NSString *dataModelListenerId;
@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) NSString *dataModelKeyParent;
@property (nonatomic, strong) NSString *dataModelKey;
@property (nonatomic, assign) BOOL handleTap;
@property (nonatomic, strong) UIColor *backgroundColorPressed;
@property (nonatomic, strong) FFTViewBehaviorLabel *viewBehavior;

- (id)initWithFrame:(CGRect)frame;

@end
