//
//  FFHttpRequest.h
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 11/7/13.
//  Copyright (c) 2013 Hans Sponberg. All rights reserved.
//

#import <Foundation/Foundation.h>
#include "java/util/HashMap.h"

typedef void (^ CallbackBlock)(id userInfo);
#define MakeCallback(method) ^(id object) { [self method:object]; }

@interface FFHttpRequest : NSObject

@property(nonatomic, assign) BOOL isBinaryData;
@property(nonatomic, strong) NSData *binaryData;
@property(nonatomic, strong) NSString *result;
@property(nonatomic, strong) NSString *serverErrorMessage;
@property(nonatomic, strong) NSString *httpAuthUsername;
@property(nonatomic, strong) NSString *httpAuthPassword;
@property(nonatomic, strong) NSMutableDictionary *userInfo; // For user purposes only
@property(nonatomic, assign) int responseCode;
@property (nonatomic, assign) JavaUtilHashMap *requestParameters;
@property(nonatomic, strong) NSString *rawPost; // To use with post if setting the raw post directory (not using parameters)

+ (FFHttpRequest *)requestWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback;
+ (FFHttpRequest *)requestPostWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback;
+ (FFHttpRequest *)requestPostWithMutipartFormBodyTypeWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback;
+ (FFHttpRequest *)requestPutWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback;

#pragma mark populating request
- (void)setValue:(NSObject *)value forKey:(NSString *)key;
- (void)sendMessageToServer;

#pragma mark getting data from results
- (NSDictionary *)resultsDictionary;
- (NSArray *)resultsArray;

@end
