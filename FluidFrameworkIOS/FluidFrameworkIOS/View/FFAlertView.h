//
//  FFAlertView.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 18/07/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTModalView;
@class Callback;

@interface FFAlertView : UIAlertView<UIAlertViewDelegate>

@property (nonatomic, strong) id<FFTCallback> callback;

@property (nonatomic, strong) FFTModalView *modalView;

@end
