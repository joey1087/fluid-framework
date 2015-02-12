//
//  FFTabBarViewController.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 27/08/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import "FFTabBarViewController.h"
#import "FFViewController.h"
#import "Screen.h"
#import "GlobalState.h"
#import "FluidApp.h"
#import "ViewHistory.h"

@interface FFTabBarViewController ()

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
}

@end
