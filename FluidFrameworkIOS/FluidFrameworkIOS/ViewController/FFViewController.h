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

//@property (nonatomic, strong, readonly) FFView *baseView;
//This was changed to read-write instead of read only because
//we're overriding the view in the subclass, this should be
//captured in a protected category
@property (nonatomic, strong) FFView *baseView;

@property (nonatomic, strong) FFTScreen *screen;

- (id)initWithScreenId:(NSString *)screenId partOfRootView:(BOOL)partOfRootView;

- (void)refreshMenuButtons;

//TODO : Below are the protected functions, they should be encapsulated into
//a protected category instead of being exposed to public 
- (CGRect)computeSizeOfView;

@end
