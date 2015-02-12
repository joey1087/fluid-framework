//
//  FFSubviewRepeat.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 17/06/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFView;

@interface FFSubviewRepeat : UIView

@property (nonatomic, strong) NSString *listener;
@property (nonatomic, weak) FFView *fluidView;

@end
