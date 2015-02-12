//
//  FFImageView.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 10/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ViewBehaviorImage.h"

@interface FFImageView : UIImageView

@property (nonatomic, strong) NSString *imageName;
@property (nonatomic, strong) FFTViewBehaviorImage_ImageBounds *imageBounds;

@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) NSString *dataModelKeyParent;
@property (nonatomic, strong) NSString *dataModelKey;

@end
