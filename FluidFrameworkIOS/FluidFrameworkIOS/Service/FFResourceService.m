//
//  FFTResourceService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 21/02/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFResourceService.h"
#include "java/io/IOException.h"

@implementation FFResourceService

- (NSString *)getResourceAsStringWithNSString:(NSString *)dir withNSString:(NSString *)name {

    NSString *path = [self pathFor:dir name:name];
    return [NSString stringWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
}

- (IOSByteArray *)getResourceAsBytesWithNSString:(NSString *)dir
                                    withNSString:(NSString *)name {
    
    NSString *path = [self pathFor:dir name:name];
    NSData *data = [NSData dataWithContentsOfFile:path];
    
    if (data == nil) {
        return nil;
    }
    
    return [IOSByteArray arrayWithBytes:data.bytes count:data.length];
}

- (void)saveResourceWithNSString:(NSString *)dir
                    withNSString:(NSString *)name
                   withByteArray:(IOSByteArray *)bytes
                     withBoolean:(BOOL)excludeFromBackup {
    
    NSString *path = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
    path = [path stringByAppendingPathComponent:dir];
    
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:NULL];
    }
    
    path = [path stringByAppendingPathComponent:name];
    
    NSData *data = [bytes toNSData];
    NSError *error;
    [data writeToFile:path options:NSDataWritingAtomic error:&error];
    
    if (error != nil) {
        @throw [[JavaIoIOException alloc] initWithNSString:[NSString stringWithFormat:@"Unable to save resource %@ %@", name, error.description]];
    }
    
    if (excludeFromBackup) {
        NSURL *url = [NSURL fileURLWithPath:path];
        [url setResourceValue:[NSNumber numberWithBool: YES]
                       forKey:NSURLIsExcludedFromBackupKey error: &error];
    }
}

- (BOOL)resourceExistsWithNSString:(NSString *)dir
                      withNSString:(NSString *)name {
 
    NSString *path = [self pathFor:dir name:name];
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    return [fileManager fileExistsAtPath:path];
}

- (NSString *)pathFor:(NSString *)dir name:(NSString *)name {
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    
    // First, try in Documents
    NSString *path = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
    path = [path stringByAppendingPathComponent:dir];
    path = [path stringByAppendingPathComponent:name];
    if ([fileManager fileExistsAtPath:path]) {
        return path;
    }
    
    // Then try in Bundle
    NSRange range = [name rangeOfString:@"." options:NSBackwardsSearch];
    NSString *base = [name substringWithRange:NSMakeRange(0, range.location)];
    NSString *type = [name substringFromIndex:range.location];
    path = [[NSBundle mainBundle] pathForResource:base ofType:type];
    return path;
}

@end
