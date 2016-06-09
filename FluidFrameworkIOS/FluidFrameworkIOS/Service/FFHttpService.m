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
#include "com/eclipsesource/json/JsonObject.h"

@interface HttpServiceTask : NSObject

@property (nonatomic, strong) id<FFTHttpServiceCallback> callback;
@property (nonatomic, strong) FFHttpRequest *request;
@end



//========================================================================================== * FFHttpService
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
    
//    //TODO : PREVIOUS CODE, CHECK WHY DO WE DO THIS ON THE MAIN THREAD
//    if (![NSThread isMainThread]) {
//        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
//            dispatch_sync(dispatch_get_main_queue(), ^{
//                [self getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback isBinary:isBinary];
//            });
//        });
//        return;
//    }
    
    if ([NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback isBinary:isBinary];
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
    
//    if (![NSThread isMainThread]) {
//        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
//            dispatch_sync(dispatch_get_main_queue(), ^{
//                [self postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
//            });
//        });
//        return;
//    }
    
    if ([NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
        });
        return;
    }
    
    FFHttpRequest *request = [FFHttpRequest requestPostWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    
    [self sendRequest:request withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth];
}

- (void)postWithNSString:(NSString *)URL
     withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_PostBodyTypeEnum:(FFTHttpService_PostBodyTypeEnum *)postBodyType
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
    if ([NSThread isMainThread]) {
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            [self postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
        });
        return;
    }
    
    FFHttpRequest *request;
    
    if ([postBodyType isEqual:FFTHttpService_PostBodyTypeEnum_get_FormData()]) {
    
        request = [FFHttpRequest requestPostWithMutipartFormBodyTypeWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    } else {
        request = [FFHttpRequest requestPostWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    }
    
    [self sendRequest:request withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth];
}

- (void)postRawWithNSString:(NSString *)URL withNSString:(NSString *)rawMessage withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {

//    if (![NSThread isMainThread]) {
//        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
//            dispatch_sync(dispatch_get_main_queue(), ^{
//                [self postRawWithNSString:URL withNSString:rawMessage withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
//            });
//        });
//        return;
//    }
    
    FFHttpRequest *request = [FFHttpRequest requestPostWithUrl:URL successCallback:MakeCallback(requestSuccess) failCallback:MakeCallback(requestFailed)];
    
    [self sendRequest:request withRaw:rawMessage withFFTHttpService_HttpAuthorization:auth];
}

- (void)putWithNSString:(NSString *)URL withJavaUtilHashMap:(JavaUtilHashMap *)parameters withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    
//    if (![NSThread isMainThread]) {
//        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
//            dispatch_sync(dispatch_get_main_queue(), ^{
//                [self putWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
//            });
//        });
//        return;
//    }
    
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






//========================================================================================== * FFHttpService
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

- (void)postWithNSString:(NSString *)URL
     withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_PostBodyTypeEnum:(FFTHttpService_PostBodyTypeEnum *)postBodyType
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
    HttpServiceTask *task = [[HttpServiceTask alloc] init];
    task.callback = callback;
    [task postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_PostBodyTypeEnum:postBodyType withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
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
