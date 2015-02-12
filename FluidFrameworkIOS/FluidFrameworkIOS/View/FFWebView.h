//
//  FFWebView.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 23/03/14.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FFWebView : UIWebView<UIWebViewDelegate>

@property (nonatomic, strong) NSString *viewPath;

- (void)handleCommand:(NSString *)command components:(NSArray *)components;

@end
