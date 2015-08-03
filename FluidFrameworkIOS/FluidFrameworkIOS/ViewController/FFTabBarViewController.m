//
//  FFTabBarViewController.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 27/08/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFTabBarViewController.h"
#import "FFViewController.h"
//#import "Screen.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "ViewHistory.h"

@interface FFTabBarViewController () <UITabBarControllerDelegate>

@end

@implementation FFTabBarViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)setSelectedViewController:(UIViewController *)selectedViewController {
    
    [super setSelectedViewController:selectedViewController];
//    if ([selectedViewController isKindOfClass:[UINavigationController class]]) {
//        UINavigationController* nav = (UINavigationController*)selectedViewController;
//        if (nav.viewControllers.count > 0) {
//            [[nav.viewControllers objectAtIndex:0] view].alpha = 0.1;
//            
//            [UIView animateWithDuration:0.2 delay:0.0 options:UIViewAnimationOptionCurveEaseInOut animations:^{
//                [[nav.viewControllers objectAtIndex:0] view].alpha  = 1.0;
//            } completion:^(BOOL finished) {
//                
//            }];
//        }
//        
//    }
    
//    NSArray *tabViewControllers = self.viewControllers;
//    UIView * fromView = self.selectedViewController.view;
//    UIView * toView = selectedViewController.view;
//    if (fromView == toView)
//        return;
//    NSUInteger fromIndex = [tabViewControllers indexOfObject:self.selectedViewController];
//    NSUInteger toIndex = [tabViewControllers indexOfObject:selectedViewController];
//    
//    [UIView transitionFromView:fromView
//                        toView:toView
//                      duration:0.3
//                       options: toIndex > fromIndex ? UIViewAnimationOptionTransitionCrossDissolve : UIViewAnimationOptionTransitionCrossDissolve
//                    completion:^(BOOL finished) {
//                        if (finished) {
//                            self.selectedIndex = toIndex;
//                        }
//                    }];
//    return;
    
}

@end
