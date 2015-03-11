//
//  FFButton.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 23/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTModalView;

@interface FFButton : UIView

@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) NSString *dataModelKeyParent;
@property (nonatomic, strong) NSString *dataModelKey;
@property (nonatomic, strong) FFTModalView *modalView;
@property (nonatomic, strong) NSString *viewId;
@property (nonatomic, strong) UIButton *button;
@property (nonatomic, strong) NSString *imageName;
@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, assign) float imageX;
@property (nonatomic, assign) float imageY;
@property (nonatomic, assign) float imageWidth;
@property (nonatomic, assign) float imageHeight;
@property (nonatomic, strong) NSString *dataModelListenerId;

+ (id)buttonWithType:(UIButtonType)buttonType frame:(CGRect)frame imageInButton:(NSString *)imageName;

- (void)userTapped;

@end
