//
//  FFTextField.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 9/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTViewPosition;
@class FFTViewBehaviorTextfield;

@interface FFTextField : UITextField

@property (nonatomic, weak) FFTViewPosition *view;
@property (nonatomic, strong) NSString *dataModelListenerId;
@property (nonatomic, strong) NSString *dataModelKeyPrefix;
@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) FFTViewBehaviorTextfield *viewBehavior;

- (id)initWithFrame:(CGRect)frame view:(FFTViewPosition *)view;

@end
