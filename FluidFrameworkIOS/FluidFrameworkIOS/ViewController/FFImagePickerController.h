//
//  FFImagePickerController.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 9/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTModalView;

@interface FFImagePickerController : UIImagePickerController

@property (nonatomic, readonly, strong) FFTModalView *modalView;

- (id)initWithModalView:(FFTModalView *)modalView;

@end
