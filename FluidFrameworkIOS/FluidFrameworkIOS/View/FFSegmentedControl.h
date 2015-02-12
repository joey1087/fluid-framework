//
//  FFSegmentedControl.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 5/06/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTView;

@interface FFSegmentedControl : UISegmentedControl

@property (nonatomic, strong) NSString *viewPath;

- (id)initWithItems:(NSArray *)items;

@end
