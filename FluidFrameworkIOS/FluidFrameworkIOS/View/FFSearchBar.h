//
//  FFSearchBar.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 5/05/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FFTViewPosition;

@interface FFSearchBar : UISearchBar

@property (nonatomic, strong) NSString *viewPath;
@property (nonatomic, strong) FFTViewPosition *fluidView;
@property (nonatomic, strong) NSString *dataModelKeyParent;
@property (nonatomic, strong) NSString *dataModelKey;

- (id)initWithFrame:(CGRect)frame viewPath:(NSString *)viewPath fluidView:(FFTViewPosition *)fluidView;

@end
