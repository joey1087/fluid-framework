//
//  FFHttpService.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 13/02/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFHttpService.h"
#include "com/sponberg/fluid/HttpServiceCallback.h"
#import "FFHttpRequest.h"
#include "java/util/HashMap.h"
#include "java/util/ArrayList.h"
#import "GTMBase64.h"
#include "com/sponberg/fluid/util/Logger.h"
#include "IOSClass.h"

@interface HttpServiceTask : NSObject

@property (nonatomic, strong) id<FFTHttpServiceCallback> callback;
@property (nonatomic, strong) FFHttpRequest *request;

@end

@implementation HttpServiceTask

- (void)getWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    [self getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback isBinary:NO];
}

- (void)getBinaryWithNSString:(NSString *)URL
          withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
   withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {

    [self getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback isBinary:YES];
}

- (void)getWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback isBinary:(BOOL)isBinary {
    
    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_sync(dispatch_get_main_queue(), ^{
                [self getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback isBinary:isBinary];
            });
        });
        return;
    }
    
    FFHttpRequest *request = [FFHttpRequest requestWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    request.isBinaryData = isBinary;
    request.requestParameters = parameters;
    
    [self sendRequest:request withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth];
}

- (void)sendRequest:(FFHttpRequest *)request withRaw:(NSString *)rawPost withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth {
    [self sendRequest:request withRaw:rawPost withJavaUtilHashMap:nil withFFTHttpService_HttpAuthorization:auth];
}

- (void)sendRequest:(FFHttpRequest *)request withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth {
    [self sendRequest:request withRaw:nil withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth];
}

- (void)sendRequest:(FFHttpRequest *)request withRaw:(NSString *)rawPost withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth {
    
    if (auth) {
        request.httpAuthUsername = [auth getUsername];
        request.httpAuthPassword = [auth getPassword];
    }
    
    if (rawPost) {
        request.rawPost = rawPost;
    }
    
    if (parameters) {
        for (NSString * __strong s in nil_chk([parameters keySet])) {
            id o = [parameters getWithId:s];
            if (o) {
                [request setValue:o forKey:s];
            } else {
                [FFTLogger warnWithId:self withNSString:@"Parameter value is null for: {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ s } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
            }
        }
    }
    
    [request sendMessageToServer];
}

- (void)postWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_sync(dispatch_get_main_queue(), ^{
                [self postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
            });
        });
        return;
    }
    
    FFHttpRequest *request = [FFHttpRequest requestPostWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    
    [self sendRequest:request withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth];
}

- (void)postRawWithNSString:(NSString *)URL withNSString:(NSString *)rawMessage withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {

    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_sync(dispatch_get_main_queue(), ^{
                [self postRawWithNSString:URL withNSString:rawMessage withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
            });
        });
        return;
    }
    
    FFHttpRequest *request = [FFHttpRequest requestPostWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    
    [self sendRequest:request withRaw:rawMessage withFFTHttpService_HttpAuthorization:auth];
}

- (void)putWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    if (![NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            dispatch_sync(dispatch_get_main_queue(), ^{
                [self putWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
            });
        });
        return;
    }
    
    FFHttpRequest *request = [FFHttpRequest requestPutWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    
    [self sendRequest:request withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth];
}

- (void)requestSuccess:(FFHttpRequest *)request {
    
    FFTHttpServiceCallback_HttpResponse *response = [[FFTHttpServiceCallback_HttpResponse alloc] init];
    response->code_ = request.responseCode;
    if (request.result) {
        response->data_ = request.result;
        [self.callback successWithFFTHttpServiceCallback_HttpResponse:response];
    } else if (request.binaryData) {
        NSString *binaryBase64 = [GTMBase64 stringByEncodingData:request.binaryData];
        response->data_ = binaryBase64;
        [self.callback successWithFFTHttpServiceCallback_HttpResponse:response];
    }
}

- (void)requestFailed:(FFHttpRequest *)request {
    
    FFTHttpServiceCallback_HttpResponse *response = [[FFTHttpServiceCallback_HttpResponse alloc] init];
    response->code_ = request.responseCode;
    response->data_ = request.serverErrorMessage;
    [self.callback failWithFFTHttpServiceCallback_HttpResponse:response];
}

@end

@implementation FFHttpService

- (void)getWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    HttpServiceTask *task = [[HttpServiceTask alloc] init];
    task.callback = callback;
    [task getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)getBinaryWithNSString:(NSString *)URL
          withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
   withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    HttpServiceTask *task = [[HttpServiceTask alloc] init];
    task.callback = callback;
    [task getBinaryWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)postWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {

    HttpServiceTask *task = [[HttpServiceTask alloc] init];
    task.callback = callback;
    [task postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)putWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {

    HttpServiceTask *task = [[HttpServiceTask alloc] init];
    task.callback = callback;
    [task putWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)postRawWithNSString:(NSString *)URL withNSString:(NSString *)rawMessage withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    HttpServiceTask *task = [[HttpServiceTask alloc] init];
    task.callback = callback;
    [task postRawWithNSString:URL withNSString:rawMessage withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

@end
