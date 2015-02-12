//
//  FFViewController.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 2/17/14.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTScreen, FFView;

@interface FFViewController : UIViewController

@property (nonatomic, strong, readonly) FFView *baseView;
@property (nonatomic, strong) FFTScreen *screen;

- (id)initWithScreenId:(NSString *)screenId partOfRootView:(BOOL)partOfRootView;

- (void)refreshMenuButtons;

@end
