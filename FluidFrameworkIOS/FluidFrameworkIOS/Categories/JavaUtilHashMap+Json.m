//
//  JavaUtilHashMap+Json.m
//  FluidFrameworkIOS
//
//  Created by Khang Nguyen on 9/06/2016.
//  Copyright © 2016 FluidFramework.org. All rights reserved.
//

#import "JavaUtilHashMap+Json.h"

#import "java/util/AbstractMap.h"
#include "java/util/Map.h"
#include "java/lang/Boolean.h"

@implementation JavaUtilHashMap (Json)

- (id)proxyForJson {
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    
    for(NSString *key in [self keySet]) {
        id object = [self getWithId:key];
        
        if ([object isKindOfClass:[self class]]) {
            [dictionary setObject:(JavaUtilHashMap *)self.proxyForJson forKey:key];
        } else if ([object isKindOfClass:[NSString class]] && ((NSString *)object).length > 0) {
                [dictionary setObject:object forKey:key];
        } else if ([object isKindOfClass:[JavaLangBoolean class]]) {
            [dictionary setObject:(((JavaLangBoolean*)object).booleanValue ? @1 : @2) forKey:key];
        }
    }
    
    return dictionary;
}

@end
