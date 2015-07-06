//
//  FFAppDelegate.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 5/03/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FFAppDelegate.h"
#include "com/sponberg/fluid/FluidApp.h"
#import "FFDataNotificationService.h"
#import "FFHttpService.h"
#import "FFResourceService.h"
#import "FFViewController.h"
#import "Tab.h"
#import "Screen.h"
#import "ViewsParser.h"
#import "FFImagePickerController.h"
#import "ModalView.h"
#import "FFNavigationViewController.h"
#import "FFViewFactoryRegistration.h"
#import "FFActionSheet.h"
#import "GlobalState.h"
#import "FFDatastoreService.h"
#import "Platforms.h"
#import "FFSystemService.h"
#import "FFSecurityService.h"
#import "FFViewFactoryRegistration.h"
#import "AttributedText.h"
#import "Layout.h"
#import "FFView.h"
#import "FFEmptyViewController.h"
#import "FFAlertView.h"
#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "ViewManager.h"
#include "FFTabBarViewController.h"
#include "FFLoggingService.h"
#include "LaunchOptionsManager.h"

#import "Callback.h"
#import <QuartzCore/QuartzCore.h>

@interface AppLoadedCallback : NSObject<FFTCallback>

@property(nonatomic, weak) FFAppDelegate* ffAppDelegate;

@end

@implementation AppLoadedCallback

- (id)initWithFFAppDelegate:(FFAppDelegate*) appDelegate {
    
    if (self = [super init]) {
        _ffAppDelegate = appDelegate;
    }
    
    return self;
}

- (void)runWithNSString:(NSString *)msg {
    if (self.ffAppDelegate) {
         if ([self.ffAppDelegate respondsToSelector:@selector(onFluidAppFinishedLoading)]) {
             [self.ffAppDelegate performSelector:@selector(onFluidAppFinishedLoading) withObject:nil afterDelay:0];
         }
    }
}

@end




@interface FFAppDelegate ()

@property (nonatomic, readwrite, strong) FFTFluidApp *fluidApp;
@property (nonatomic, readwrite, strong) UIView *modalView;
@property (nonatomic, readwrite, assign) BOOL started;

@end

@implementation FFAppDelegate

- (void)setupApp:(NSDictionary *)launchOptions {
    
    float version = [[UIDevice currentDevice].systemVersion floatValue];
    
    FFTFluidApp *fluidApp = [FFTGlobalState fluidApp];
    self.fluidApp = fluidApp;
    
    [self parseLaunchOptions:launchOptions];
    
    [fluidApp setPlatformWithNSString:FFTPlatforms_IOS_];
    
    [self registerFluidViews];
    
    [fluidApp setHttpServiceWithFFTHttpService:[[FFHttpService alloc] init]];
    
    [fluidApp setUiServiceWithFFTUIService:self];
    
    [fluidApp setSystemServiceWithFFTSystemService:[[FFSystemService alloc] init]];
    
    [fluidApp setResourceServiceWithFFTResourceService:[[FFResourceService alloc] init]];
    
    [fluidApp setLoggingServiceWithFFTLoggingService:[[FFLoggingService alloc] init]];
    
    FFSecurityService *ss = [[FFSecurityService alloc] init];
    [fluidApp setSecurityServiceWithFFTSecurityService:ss];
    if ([fluidApp getPasswordProvider]) {
        ss.passwordProvider = [fluidApp getPasswordProvider];
    }
    
    [fluidApp setDatastoreServiceWithFFTDatastoreService:[[FFDatastoreService alloc] init]];
    
    [fluidApp setBaseUnitWithDouble:[self computeBaseUnit]];
    
    [fluidApp setDevicePixelMultiplierWithDouble:1.0]; // On retina, 1 pixel will draw as 2 pixels
    
    double deviceActualPixelMultiplier = 1.0f / [[UIScreen mainScreen] scale];
    [fluidApp setDevicePixelActualMultiplierWithDouble:deviceActualPixelMultiplier];
    
    self.dataNotificationService = [[FFDataNotificationService alloc] init];
    
    [fluidApp initialize__];
    
    // hstdbc do this now? at least show splash
    // [self setupWindow];
    [self keepSplashScreen];
    
    [self performSelector:@selector(load) withObject:nil afterDelay:0];
}

- (void)load {
    AppLoadedCallback* callback = [[AppLoadedCallback alloc] initWithFFAppDelegate:self];
    [self.fluidApp load__WithFFTCallback:callback];
}

- (void)onFluidAppFinishedLoading{
    /*
     * This is called when the fluidApp has successfully loaded
     * subclass can overide this method if there's some processing
     * that needs to be done.
     */
}

- (void)keepSplashScreen {
    
    // iOS removes the splash screen after appDidFinishLaunchingWithOptions
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    UIImageView *view = [[UIImageView alloc] initWithFrame:self.window.frame];
    if ([[UIScreen mainScreen] bounds].size.height == 568) {
        [view setImage:[UIImage imageNamed:@"default-568h"]];
    } else {
        [view setImage:[UIImage imageNamed:@"default"]];
    }
    [self.window addSubview:view];
    self.window.rootViewController = [[FFEmptyViewController alloc] init];
    
    [self.window makeKeyAndVisible];
}

- (void)registerFluidViews {
    [FFViewFactoryRegistration registerViews:[FFTGlobalState fluidApp]];
}

- (float)computeBaseUnit {
    
    float scale = 1;
    if ([[UIScreen mainScreen] respondsToSelector:@selector(scale)]) {
        scale = [[UIScreen mainScreen] scale];
    }
    float dpi = 163 * scale;
    
    float dpmm = dpi / 25.4;
    
    float baseUnit = dpmm / scale;
    
    return baseUnit; // 6.41732264 for iPhone 5
}

- (void)setupWindow:(NSString *)showScreenId {
    
    self.window.rootViewController = nil;
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    
    id<JavaUtilList> list = [[FFTGlobalState fluidApp] getTabs];
    NSMutableArray *tabArray = [NSMutableArray array];
    for (FFTTab * __strong view in nil_chk(list)) {
        FFViewController *fvc = [self createFFViewController:[view getScreenId] partOfRootView:YES];
        UINavigationController *nav = [[FFNavigationViewController alloc] initWithRootViewController:fvc];
        nav.title = view->label_;
        NSString *imageName = [view getImage];
        NSString *selectedImage = [view getSelectedImage];
        if (imageName) {
            if (!selectedImage) {
                selectedImage = imageName;
            }
            
            if ([nav.tabBarItem respondsToSelector:@selector(initWithTitle:image:selectedImage:)]) {
                nav.tabBarItem = [[UITabBarItem alloc] initWithTitle:view->label_ image:[[UIImage imageNamed:imageName] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal] selectedImage:[[UIImage imageNamed:selectedImage] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
            } else {
                nav.tabBarItem = [[UITabBarItem alloc] initWithTitle:view->label_ image:[UIImage imageNamed:imageName] tag:0];
            }
        }
        [tabArray addObject:nav];
        
        IOSObjectArray *property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-nav-bar-tint" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
        NSString *colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
        if (colorProperty) {
            FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
            UIColor *color = [FFView color:fftColor];
            nav.navigationBar.tintColor = color;
        }
    }
    
    IOSObjectArray *property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-tab-bar-tint" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
    NSString *colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
    if (colorProperty && [[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0) {
        FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
        UIColor *color = [FFView color:fftColor];
        
        [[UITabBar appearance] setTintColor:color];
    }

    property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-tab-bar-bar-tint" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
    colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
    if (colorProperty && [[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0) {
        FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
        UIColor *color = [FFView color:fftColor];
        
        [[UITabBar appearance] setBarTintColor:color];
    }
    
    property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios6-tab-bar-background" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
    colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
    if (colorProperty && [[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
        FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
        UIColor *color = [FFView color:fftColor];
        
        [[UITabBar appearance] setTintColor:color];
    }
    
    UITabBarController *tabController = [[FFTabBarViewController alloc] init];
    self.tabController = tabController;
    tabController.viewControllers = tabArray;
    
    property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-tab-bar-border-color" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
    colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
    if (colorProperty) {
        FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
        UIColor *color = [FFView color:fftColor];
        tabController.tabBar.layer.borderWidth = 0.50;
        tabController.tabBar.layer.borderColor = color.CGColor;
    }
    
    self.window.backgroundColor = [UIColor whiteColor];
    self.window.rootViewController = tabController;
    [self.window addSubview:tabController.view];
    [self.window makeKeyAndVisible];
    
    if (showScreenId != [self currentScreenId]) {
        //[self pushLayoutWithNSString:showScreenId];
        [self setLayoutWithNSString:showScreenId withBoolean:NO];
    }
        
    //TODO : why do we set up a webview here and load HTML here
    UIWebView *view = [[UIWebView alloc] initWithFrame:CGRectMake(0, 0, 0, 0)];
    [view loadHTMLString:@"<html><body></body></html>" baseURL:nil];
}

- (FFViewController *)createFFViewController:(NSString *)screenId partOfRootView:(BOOL)partOfRootView {
    
    return [[FFViewController alloc] initWithScreenId:screenId partOfRootView:YES];
}

- (void)pushLayoutWithNSString:(NSString *)screenId {
    
    if ([NSThread isMainThread]) {
        
        // Keep this in sync with setLayoutStack
        
        FFTScreen *screen = [[FFTGlobalState fluidApp] getScreenWithNSString:screenId];
        
        [self setBackButton:screen];
        
        FFViewController *fvc = [self createFFViewController:screenId partOfRootView:YES];
        
        if ([screen isHideBackButton]) {
            
            fvc.navigationItem.hidesBackButton = YES;
        } else {
            
            fvc.navigationItem.hidesBackButton = NO;
        }
        
        if (![screen isShowTabBar]) {
            
            fvc.hidesBottomBarWhenPushed = YES;
        }

        if ([screen isShowStatusBar]) {
            [UIApplication sharedApplication].statusBarHidden = NO;
        } else {
            [UIApplication sharedApplication].statusBarHidden = YES;
        }
        
        // End: Keep this in sync with setLayoutStack 
        
        FFNavigationViewController *controller = [self currentNavigationController];
        FFNavigationViewController *vc = (FFNavigationViewController *) [controller presentedViewController];
        if (vc) {
            [vc pushViewController:fvc animated:YES];
        } else {
            [[self currentNavigationController] pushViewController:fvc animated:YES];
        }
    } else {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self pushLayoutWithNSString:screenId];
            });
        });
    }
}

- (void)setBackButton:(FFTScreen *)screen {
    
    if ([screen getBackButtonText]) {
        
        [self currentNavigationController].topViewController.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:[[screen getBackButtonText] trim] style:UIBarButtonItemStylePlain target:nil action:nil];
    } else {
        // use default
        [self currentNavigationController].topViewController.navigationItem.backBarButtonItem = nil;
    }
}

- (NSString *)currentScreenId {
    FFViewController *fvc = [[[self currentNavigationController] viewControllers] lastObject];
    return [fvc.screen getScreenId];
}

- (void)popLayout {
    if ([NSThread isMainThread]) {
        
        FFNavigationViewController *controller = [self currentNavigationController];
        FFNavigationViewController *vc = (FFNavigationViewController *) [controller presentedViewController];
        if (vc) {
            
            [vc popViewControllerAnimated:YES];
            
            if ([[[UIDevice currentDevice] systemVersion] floatValue] < 7.0) {
                
                FFNavigationViewController *vc = (FFNavigationViewController *) [controller presentedViewController];
                
                FFViewController *fvc;
                if (vc) {
                    
                    fvc = (FFViewController *) vc.topViewController;
                } else {
                    
                    fvc = (FFViewController *) [self currentNavigationController].topViewController;
                }
                
                [fvc.baseView createOrLayoutViews];
            }
            
        } else {
            
            [[self currentNavigationController] popViewControllerAnimated:YES];
        }
    } else {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self popLayout];
            });
        });
    }
}

- (void)setLayoutWithNSString:(NSString *)screenId withBoolean:(BOOL)stack {

    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self setLayoutWithNSString:screenId withBoolean:stack];
            });
        });
        return;
    }
    
    if (!stack) {

        // If we're changing tabs, just change the tab
        
        // If screenId is a tab
        
        if ([self selectTab:screenId]) {
            /*
             * This is to pop to the root of the current nav on
             * the current selected tab.
             */
            UITabBarController *tabController = self.tabController;
            for (UIViewController *controller in tabController.viewControllers) {
                FFNavigationViewController *nav = (FFNavigationViewController *) controller;
                FFViewController *fvc = [nav.viewControllers firstObject];
                if ([[fvc.screen getScreenId] isEqualToString:screenId]) {
                    [nav popToRootViewControllerAnimated:NO];
                    break;
                }
            }
            return;
        }
        
        void (^setNewControllerAndStyle)(void) = ^{
            
            FFViewController *fvc = [self createFFViewController:screenId partOfRootView:!stack];
            //[[self currentNavigationController] pushViewController:fvc animated:NO];
            //[fvc.navigationController setNavigationBarHidden:NO];
            //CGRect frame = fvc.navigationController.view.frame;
            //fvc.navigationController = [[FFNavigationViewController alloc] initWithRootViewController:fvc];
            //self.window.rootViewController = fvc;
            
            FFNavigationViewController *rootNavController = [[FFNavigationViewController alloc] initWithRootViewController:fvc];
            
            IOSObjectArray *property = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-nav-bar-tint" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
            NSString *colorProperty = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:property];
            if (colorProperty) {
                FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:colorProperty];
                UIColor *color = [FFView color:fftColor];
                rootNavController.navigationBar.tintColor = color;
            }
            
            IOSObjectArray *statusBarColorProperty = [IOSObjectArray arrayWithObjects:(id[]){ @"defaults", @"colors", @"ios-status-bar-tint" } count:3 type:[IOSClass classWithClass:[NSObject class]]];
            NSString *statusBarColorPropertyString = [[FFTGlobalState fluidApp] getSettingWithNSStringArray:statusBarColorProperty];
            if (statusBarColorPropertyString) {
                /*
                 * This is to change to status bar color indepently off the navigation bar color
                 */
                if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 7.0) {
                    FFTColor *fftColor = [[[FFTGlobalState fluidApp] getViewManager] getColorWithNSString:statusBarColorPropertyString];
                    UIColor *color = [FFView color:fftColor];
                    UIView *topView=[[UIView alloc] initWithFrame:CGRectMake(0, 0,320, 20)];
                    topView.backgroundColor = color;
                    [rootNavController.view addSubview:topView];
                }
            }
            
            self.window.rootViewController = rootNavController;
        };
        
        FFNavigationViewController *controller = [self currentNavigationController];
        if ([controller presentedViewController]) {
            
            [controller dismissViewControllerAnimated:NO completion:^{
                setNewControllerAndStyle();
            }];
        } else if ([controller respondsToSelector:@selector(popViewControllerAnimated:)]) {
            
            [[self currentNavigationController] popViewControllerAnimated:YES];
            setNewControllerAndStyle();
        }

    } else {
        FFViewController *fvc = [self createFFViewController:screenId partOfRootView:!stack];
        fvc.navigationItem.hidesBackButton = YES;
        [[self currentNavigationController] pushViewController:fvc animated:NO];
        
        /* For pop up from bottom
        UINavigationController *navigationController =
        [[FFNavigationViewController alloc] initWithRootViewController:fvc];

        [[self currentNavigationController] presentViewController:navigationController animated:YES completion:nil];*/
    }
    
    //FFViewController *fvc = [[FFViewController alloc] initWithScreenId:screenId partOfRootView:!stack];
    //[[self currentNavigationController] pushViewController:fvc animated:NO];
    //self.window.rootViewController = fvc;
}

- (BOOL)selectTab:(NSString *)screenId {
    
    //UITabBarController *tabController = (UITabBarController *) self.window.rootViewController;
    UITabBarController *tabController = self.tabController;
    
    for (UIViewController *controller in tabController.viewControllers) {
        FFNavigationViewController *nav = (FFNavigationViewController *) controller;
        FFViewController *fvc = [nav.viewControllers firstObject];
        if ([[fvc.screen getScreenId] isEqualToString:screenId]) {
            [tabController setSelectedViewController:controller];
            
            //[nav popToRootViewControllerAnimated:NO];
            
            /*
             while ([nav.viewControllers count] > 1) {
             [nav popViewControllerAnimated:NO];
             }*/
            
            if (![fvc.screen isShowTabBar]) {
                
                fvc.hidesBottomBarWhenPushed = YES;
            }
            
            if ([fvc.screen isShowStatusBar]) {
                [UIApplication sharedApplication].statusBarHidden = NO;
            } else {
                [UIApplication sharedApplication].statusBarHidden = YES;
            }
            
            self.window.rootViewController = tabController;
            
            return YES;
        }
    }
    return NO;
}

- (void)showModalViewWithFFTModalView:(FFTModalView *)modalView {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_sync(dispatch_get_main_queue(), ^{
            [self showModalViewWithFFTModalViewHelper:modalView];
        });
    });
}

- (void)showModalViewWithFFTModalViewHelper:(FFTModalView *)modalView {
    
    if (!modalView) {
        return;
    }
    
    if ([[modalView getSystemId] isEqualToString:FFTModalView_FluidLayout_]) {
        
        NSString *screenId = [modalView getUserData];
        
        int screenWidth = [self getScreenWidthInPixels];
        int screenHeight = [self getScreenHeightInPixels];
        
        int width = screenWidth * .9;
        int x = (screenWidth - width) / 2;
        
        FFTLayout *layout = [[[FFTGlobalState fluidApp] getScreenWithNSString:screenId] getLayout];

        BOOL landscape = [[FFTGlobalState_fluidApp__ getUiService] isOrientationLandscape];
        int height = [layout calculateHeightWithBoolean:landscape withFloat:width withNSString:nil];
        int y = (screenHeight - height) / 2;
        
        CGRect bounds = CGRectMake(x, y, width, height);
        
        [self.modalView removeFromSuperview];
        self.modalView = nil;

        self.modalView = [self createModalView:bounds screenId:screenId layout:layout modalView:modalView];
        [[self window] addSubview:self.modalView];
        
    } else if ([[modalView getSystemId] isEqualToString:FFTModalView_FluidLayoutFullScreen_]) {
        
        NSString *screenId = [modalView getUserData];

        FFTScreen *screen = [[FFTGlobalState fluidApp] getScreenWithNSString:screenId];
        
        [self setBackButton:screen];
        
        FFViewController *fvc = [self createFFViewController:screenId partOfRootView:YES];
        
        fvc.navigationItem.hidesBackButton = YES;
        
        FFNavigationViewController *navigationController = [[FFNavigationViewController alloc] initWithRootViewController:fvc];
        navigationController.navigationBar.tintColor = [self currentNavigationController].navigationBar.tintColor;

        if ([screen isShowStatusBar]) {
            [UIApplication sharedApplication].statusBarHidden = NO;
        } else {
            [UIApplication sharedApplication].statusBarHidden = YES;
        }
        
        UIViewController *controller = [self currentNavigationController];
        if ([controller presentedViewController]) {
            
            [[controller presentedViewController] presentViewController:navigationController animated:YES completion:nil];
        } else {
            
            [controller presentViewController:navigationController animated:YES completion:nil];
        }
        
    } else if ([[modalView getSystemId] isEqualToString:FFTModalView_Confirmation_]) {
        
        FFTModalView_ModalViewConfirmation *userData = [modalView getUserData];
        
        FFAlertView *alert = [[FFAlertView alloc] initWithTitle:[userData getTitle]
                                                        message:[userData getMessage]
                                                       delegate:nil
                                              cancelButtonTitle:[userData getCancel]
                                              otherButtonTitles:nil];

        [alert addButtonWithTitle:[userData getOk]];
        
        alert.modalView = modalView;
        [alert show];
        
    } else if ([[modalView getSystemId] isEqualToString:FFTModalView_WaitingDialog_]) {
        
        FFTModalView_ModalViewWaitingDialog *userData = [modalView getUserData];
        
        FFAlertView *alert = [[FFAlertView alloc] initWithTitle:[userData getTitle]
                                                        message:[userData getMessage]
                                                       delegate:nil
                                              cancelButtonTitle:nil
                                              otherButtonTitles:nil];
        
        alert.modalView = modalView;
        
        self.modalView = alert;
        
        [alert show];
        
    } else if ([[modalView getSystemId] isEqualToString:FFTModalView_ImagePicker_]) {
        
        // If camera is available, let user choose between camera or library
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            
            FFActionSheet *actionSheet = [[FFActionSheet alloc]
                                             initWithTitle:nil
                                             delegate:[self currentNavigationController]
                                             cancelButtonTitle:@"Cancel"
                                             destructiveButtonTitle:nil
                                             otherButtonTitles:@"Take Photo", @"Choose from Library", nil];
            actionSheet.modalView = modalView;
            actionSheet.tag = ACTION_SHEET_PHOTO_OR_LIBRARY;
        
            [actionSheet showInView:[self currentNavigationController].view];
        } else {
            
            // Pop up gallery
            FFImagePickerController *picker = [[FFImagePickerController alloc] initWithModalView:modalView];
            picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
            picker.delegate = [self currentNavigationController];
            
            [[self currentNavigationController] presentViewController:picker animated:YES completion:^{}];
        }
    } else if ([[modalView getSystemId] isEqualToString:FFTModalView_Custom_]){
        [self showCustomModalView:modalView];
    }
}

//Subclass to override this function
- (void)showCustomModalView:(FFTModalView *)modalView {
    
}

- (UIView *)createModalView:(CGRect)bounds screenId:(NSString *)screenId layout:(FFTLayout *)layout modalView:(FFTModalView *)fftModalView {
    
    UIView *view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    view.backgroundColor = [UIColor clearColor];
    
    UIView *view2 = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    view2.backgroundColor = [UIColor blackColor];
    view2.alpha = .6;
    
    FFView *modalView = [[FFView alloc] initWithFrame:bounds viewPath:screenId layout:layout rootFFView:nil inTableView:nil];
    modalView.modalView = fftModalView;
    [fftModalView setFluidDataWithId:view];
    
    [view addSubview:view2];
    [view addSubview:modalView];
    
    return view;
}

- (void)dismissModalViewWithFFTModalView:(FFTModalView *)modalView {
    
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        dispatch_async(dispatch_get_main_queue(), ^{
            
            if ([self.modalView isKindOfClass:[UIAlertView class]]) {
                UIAlertView *alert = (UIAlertView *) self.modalView;
                [alert dismissWithClickedButtonIndex:0 animated:YES];
            } else {
                [self.modalView removeFromSuperview];
            }
            self.modalView = nil;
        });
    });
}

- (void)closeCurrentLayout {

    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self closeCurrentLayout];
            });
        });
        return;
    }
    
    FFNavigationViewController *controller = [self currentNavigationController];
    if ([controller presentedViewController]) {
        
        [[self currentNavigationController] dismissViewControllerAnimated:YES completion:nil];
    } else {
        
        [[self currentNavigationController] popViewControllerAnimated:NO];
    }
    
}

- (void)showAlertWithNSString:(NSString *)title withNSString:(NSString *)message {
    [self showAlertWithNSString:title withNSString:message withFFTCallback:nil];
}

- (void)showAlertWithNSString:(NSString *)title
                 withNSString:(NSString *)message
              withFFTCallback:(id<FFTCallback>)callback {

    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self showAlertWithNSString:title withNSString:message];
            });
        });
        return;
    }
    
    FFAlertView *alert = [[FFAlertView alloc] initWithTitle:title
                                                    message:message
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    alert.callback = callback;
    [alert show];
}

- (float)computeHeightOfTextWithNSString:(NSString *)text
                               withFloat:(float)width
                            withNSString:(NSString *)fontName
                               withFloat:(float)fontSizeInUnits {
    FFTAttributedText *attText = [[FFTAttributedText alloc] initWithNSString:text];
    return [FFViewFactoryRegistration computeHeightOfText:attText width:width height:MAXFLOAT fontName:fontName fontSizeInUnits:fontSizeInUnits minFontSizeInUnits:0 maxFontSizeInUnits:0 defaultColor:nil].bounds.size.height;
}

- (FFNavigationViewController *)currentNavigationController {
    //UITabBarController *tabController = (UITabBarController *) self.window.rootViewController;
    
    if (self.window.rootViewController == self.tabController) {
        UITabBarController *tabController = self.tabController;
        
        FFNavigationViewController *nav = (FFNavigationViewController *) tabController.selectedViewController;
        
        return nav;
    } else if ([self.window.rootViewController isKindOfClass:[FFEmptyViewController class]]) {
        return nil;
    } else {
        return (FFNavigationViewController *) self.window.rootViewController;
    }
    
    /*
    UITabBarController *tabController = self.tabController;
    
    FFNavigationViewController *nav = (FFNavigationViewController *) tabController.selectedViewController;
    
    return nav;*/
}

- (void)removeSplashScreenWithNSString:(NSString *)firstScreenId
                           withBoolean:(BOOL)insteadShowCurrentScreenIfAny {
    
    NSString *screenId = [self currentScreenId];
    
    NSString *showScreenId = (insteadShowCurrentScreenIfAny && screenId != nil) ? screenId : firstScreenId;
    
    if (showScreenId == nil) {
         @throw @"Must set showScreenId";
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_sync(dispatch_get_main_queue(), ^{
            if (self.started) {
                
                if (!insteadShowCurrentScreenIfAny) {
                    [self setLayoutWithNSString:showScreenId withBoolean:NO];
                }
                
                [self.fluidApp restart];
            } else {
                [self setupAndStart:showScreenId];
            }
        });
    });
}

- (void)setupAndStart:(NSString *)showScreenId {
    
    self.started = YES;
    
    // hstdbc do this here?
    [self setupWindow:showScreenId];
    
    [self.fluidApp start]; // only first time
}

- (int)getScreenWidthInPixels {
    return [UIScreen mainScreen].bounds.size.width;
}

- (int)getScreenHeightInPixels {
    return [UIScreen mainScreen].bounds.size.height;
}

- (void)refreshMenuButtons {
 
    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self refreshMenuButtons];
            });
        });
        return;
    }
    
    FFViewController *fvc = [[[self currentNavigationController] viewControllers] lastObject];
    [fvc refreshMenuButtons];
}

- (void)grabFocusForViewWithNSString:(NSString *)viewId {

    FFViewController *fvc = [[[self currentNavigationController] viewControllers] lastObject];
    [fvc.baseView grabFocusForView:viewId];
}

- (void)hideKeyboard {

    FFViewController *fvc = [[[self currentNavigationController] viewControllers] lastObject];
    [fvc.baseView hideKeyboard];
}

- (void)clearViewStack {
    
    if ([NSThread isMainThread]) {
        
        [[self currentNavigationController] popToRootViewControllerAnimated:NO];
        
        /*
        while ([[[self currentNavigationController] viewControllers] count] > 1) {
            NSLog(@"hstdbc popLayout");
            [self popLayout];
        }*/
    } else {
        
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self clearViewStack];
            });
        });
    }
}

- (void)setLayoutStackWithNSStringArray:(IOSObjectArray *)screenIds {
    
    FFNavigationViewController *controller = [self currentNavigationController];
    if ([controller presentedViewController]) {
        
        [controller dismissViewControllerAnimated:NO completion:^{
            [self setLayoutStackWithNSStringArray:screenIds];
        }];
        return;
    }
    
    NSString *tab = [screenIds objectAtIndex:0];
    
    if (![self selectTab:tab]) {
        @throw @"First screenId of view stack is expected to be a tab";
    }
    
    UIViewController *topViewController = [[self currentNavigationController].viewControllers firstObject];
    
    for (int index = 1; index < [[self currentNavigationController].viewControllers count]; index++) {
        
        FFViewController *fvc = [[self currentNavigationController].viewControllers objectAtIndex:index];
        
        [fvc.baseView cleanup];
    }
    
    NSMutableArray *viewControllers = [NSMutableArray array];
    [viewControllers addObject:topViewController];
    
    for (int index = 1; index < [screenIds count]; index++) {
        
        BOOL isTopScreen = index == [screenIds count] - 1;
        
        NSString *screenId = [screenIds objectAtIndex:index];
        
        // Keep this in sync with pushLayout
        
        FFTScreen *screen = [[FFTGlobalState fluidApp] getScreenWithNSString:screenId];

        // Keep in sync with [self setBackButton]
        if ([screen getBackButtonText]) {
            
            topViewController.navigationItem.backBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:[screen getBackButtonText] style:UIBarButtonItemStylePlain target:nil action:nil];
        } else {
            // use default
            topViewController.navigationItem.backBarButtonItem = nil;
        }
        // End: Keep in sync with [self setBackButton]
        
        FFViewController *fvc = [self createFFViewController:screenId partOfRootView:YES];
        
        if ([screen isHideBackButton]) {
            
            fvc.navigationItem.hidesBackButton = YES;
        } else {
            
            fvc.navigationItem.hidesBackButton = NO;
        }
        
        if (![screen isShowTabBar]) {
            
            fvc.hidesBottomBarWhenPushed = YES;
        }
        
        if (isTopScreen && [screen isShowStatusBar]) {
            [UIApplication sharedApplication].statusBarHidden = NO;
        } else {
            [UIApplication sharedApplication].statusBarHidden = YES;
        }
        
        // End: Keep this in sync with pushLayout
        
        topViewController = fvc;
        
        [viewControllers addObject:fvc];
    }
    
    [[self currentNavigationController] setViewControllers:viewControllers animated:NO];
}


- (void)scrollToBottomWithNSString:(NSString *)viewPath
                      withNSString:(NSString *)viewId {
    
    if ([NSThread isMainThread]) {
        
        FFNavigationViewController *controller = [self currentNavigationController];
        FFNavigationViewController *vc = (FFNavigationViewController *) [controller presentedViewController];
        
        FFViewController *fvc;
        if (vc) {
            
            fvc = (FFViewController *) vc.topViewController;
        } else {
            
            fvc = (FFViewController *) [self currentNavigationController].topViewController;
        }
        
        [fvc.baseView scrollToBottomWithNSString:viewPath withNSString:viewId];
    } else {
        dispatch_async(dispatch_get_global_queue(0, 0), ^{
            dispatch_async(dispatch_get_main_queue(), ^{
                [self scrollToBottomWithNSString:viewPath withNSString:viewId];
            });
        });
    }
}

- (BOOL)isOrientationLandscape {

    UIDeviceOrientation orientation = [[UIDevice currentDevice] orientation];
    
    if (!orientation) {
        return false;
    } else {
        return orientation == UIDeviceOrientationLandscapeLeft || orientation == UIDeviceOrientationLandscapeRight;
    }
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    [self.fluidApp reloadAsync];
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

- (void)parseLaunchOptions:(NSDictionary *)launchOptions {
    
    // For now just populate remote notifications
    
    JavaUtilHashMap *data = [[JavaUtilHashMap alloc] init];
    
    NSDictionary* userInfo = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (userInfo) {
        
        NSDictionary *aps = [userInfo objectForKey:@"aps"];
        for (NSString *key in aps.allKeys) {
            NSString *value = [NSString stringWithFormat:@"%@", [aps objectForKey:key]];
            [data putWithId:key withId:value];
        }
    }
    
    [[[FFTGlobalState fluidApp] getLaunchOptionsManager] setPushNotificationWithJavaUtilHashMap:data];
}

- (NSString*)hexString:(NSData *)str {
    unichar* hexChars = (unichar*)malloc(sizeof(unichar) * (str.length*2));
    unsigned char* bytes = (unsigned char*) str.bytes;
    for (NSUInteger i = 0; i < str.length; i++) {
        unichar c = bytes[i] / 16;
        if (c < 10) c += '0';
        else c += 'A' - 10;
        hexChars[i*2] = c;
        c = bytes[i] % 16;
        if (c < 10) c += '0';
        else c += 'A' - 10;
        hexChars[i*2+1] = c;
    }
    NSString* retVal = [[NSString alloc] initWithCharactersNoCopy:hexChars
                                                           length:(str.length * 2)
                                                     freeWhenDone:YES];
    return retVal;
}

- (NSString *)getCurrentScreenId {
    
    NSString* returnStr = Nil;

    FFNavigationViewController* nav = [self currentNavigationController];
    
    if (nav && nav.visibleViewController) {
        
        if ([[self currentNavigationController].visibleViewController isKindOfClass:[FFViewController class]]) {
            FFViewController *fvc = (FFViewController*)[self currentNavigationController].visibleViewController;
            
            returnStr = [[fvc screen] getScreenId];
        }
    }
    
    return returnStr;
}

@end






















