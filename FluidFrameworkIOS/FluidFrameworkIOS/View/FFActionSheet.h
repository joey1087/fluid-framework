//
//  FFActionSheet.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 11/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTModalView;

@interface FFActionSheet : UIActionSheet

@property (nonatomic, readwrite, strong) FFTModalView *modalView;

@end
