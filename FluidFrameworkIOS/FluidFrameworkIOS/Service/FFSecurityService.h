//
//  FFSecurityService.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 4/06/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SecurityService.h"

@interface FFSecurityService : NSObject<FFTSecurityService, FFTSecurityService_PasswordProvider>

@property (nonatomic, strong) id<FFTSecurityService_PasswordProvider> passwordProvider;

@end
