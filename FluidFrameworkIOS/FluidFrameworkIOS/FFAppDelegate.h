//
//  FFAppDelegate.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 5/03/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UIService.h"
#import "FFFluidAppDelegate.h"

@class FFViewController, FFTScreen;
@class FFNavigationViewController;

@interface FFAppDelegate : UIResponder <UIApplicationDelegate, FFTUIService, FFFluidAppDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (strong, nonatomic) FFDataNotificationService *dataNotificationService;

@property (nonatomic, readwrite, strong) UITabBarController *tabController;

@property (nonatomic) NSArray* tabArray;

//contains pairs of modal - presenting view controller
@property (nonatomic) NSMutableDictionary* modalControllers;

- (void)setupApp:(NSDictionary *)launchOptions;

- (NSString*)hexString:(NSData *)str;

- (void)registerFluidViews;

- (void)setupWindow:(NSString *)showScreenId;

- (FFViewController *)createFFViewController:(NSString *)screenId partOfRootView:(BOOL)partOfRootView;

- (void)setBackButton:(FFTScreen *)screen;

- (void)parseLaunchOptions:(NSDictionary *)launchOptions;

- (FFNavigationViewController *)currentNavigationController;

- (void)showModalViewWithFFTModalViewHelper:(FFTModalView *)modalView;

- (UIViewController*)createAppContainerViewController:(NSMutableArray*)tabControllers;
@end
