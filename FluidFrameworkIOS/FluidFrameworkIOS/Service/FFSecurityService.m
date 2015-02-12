//
//  FFSecurityService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 4/06/2014.
//  Copyright (c) 2014 FluidFramework.org. All rights reserved.
//

#import <CommonCrypto/CommonHMAC.h>

#import "FFSecurityService.h"
#import "Base64.h"

@implementation FFSecurityService

- (id)init {
    if (self == [super init]) {
        self.passwordProvider = self;
    }
    return self;
}

- (NSString *)hmacBase64WithNSString:(NSString *)data
                        withNSString:(NSString *)key
                        withNSString:(NSString *)salt {

    data = [NSString stringWithFormat:@"%@_%@", salt, data];
    
    const char *cKey  = [key cStringUsingEncoding:NSASCIIStringEncoding];
    const char *cData = [data cStringUsingEncoding:NSASCIIStringEncoding];
    
    unsigned char cHMAC[CC_SHA256_DIGEST_LENGTH];
    
    CCHmac(kCCHmacAlgSHA256, cKey, strlen(cKey), cData, strlen(cData), cHMAC);
    
    NSData *HMAC = [[NSData alloc] initWithBytes:cHMAC
                                          length:sizeof(cHMAC)];
    
    NSString *hash;
    if ([HMAC respondsToSelector:@selector(base64EncodedStringWithOptions:)]) {
        hash = [HMAC base64EncodedStringWithOptions:kNilOptions];  // iOS 7+
    } else {
        hash = [HMAC base64Encoding];                              // pre iOS7
    }
    
    return hash;
}

- (BOOL)hasUserSalt {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *salt = [defaults objectForKey:@"fluidUS"];
    if (salt) {
        return YES;
    } else {
        return NO;
    }
}

- (NSString *)getUserSalt {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *salt = [defaults objectForKey:@"fluidUS"];
    if (!salt) {
        salt = [[NSUUID UUID] UUIDString];
        [defaults setObject:salt forKey:@"fluidUS"];
        [defaults synchronize];
    }
    return salt;
}

- (id<FFTSecurityService_PasswordProvider>)getPasswordProvider {
    return self.passwordProvider;
}

- (NSString *)getHmacKeyFluidDatastoreParameters {
    // Provide a default in case the client doesn't set one
    return @"zecv1]843[oiuyHSfoiuwRh823#$2asdpf)...@";
}

@end
