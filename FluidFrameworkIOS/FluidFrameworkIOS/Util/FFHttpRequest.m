//
//  FFHttpRequest.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 11/7/13.
//  Copyright (c) 2013 Hans Sponberg. All rights reserved.
//

#import "FFHttpRequest.h"
#import "SBJSON.h"
#import "GTMBase64.h"

#define API_TIMEOUT 10

typedef enum {
    Get,
    Post,
    Put
} HttpRequestMethod;

@interface FFHttpRequest (){
    BOOL finished;
}

@property(nonatomic, strong) NSMutableData *webData;
@property(nonatomic, strong) NSMutableDictionary *map;
@property(nonatomic, strong) NSData *file;
@property(nonatomic, strong) NSString *url;
@property(nonatomic, copy) CallbackBlock successCallback;
@property(nonatomic, copy) CallbackBlock failCallback;
@property(nonatomic, strong) NSString *userSignKey;
@property(nonatomic, strong) NSMutableDictionary *params;
//@property(nonatomic, assign) HttpRequestFailReason failReason;
@property(nonatomic, strong) NSMutableURLRequest *theRequest;
@property(nonatomic, assign) int attempt;
@property(nonatomic, assign) int maxAttempts;
@property(nonatomic, assign) HttpRequestMethod requestMethod;

@end

@implementation FFHttpRequest

+ (FFHttpRequest *)requestWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback {
    return [[self alloc] initWithUrl:url successCallback:successCallback failCallback:failCallback requestMethod:Get];
}

+ (FFHttpRequest *)requestPostWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback {
    return [[self alloc] initWithUrl:url successCallback:successCallback failCallback:failCallback requestMethod:Post];
}

+ (FFHttpRequest *)requestPutWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback {
    return [[self alloc] initWithUrl:url successCallback:successCallback failCallback:failCallback requestMethod:Put];
}

- (id)initWithUrl:(NSString *)url successCallback:(CallbackBlock)successCallback failCallback:(CallbackBlock)failCallback requestMethod:(HttpRequestMethod)requestMethod {
    self = [super init];
    if (self) {
        self.url = url;
        self.map = [NSMutableDictionary dictionary];
        self.attempt = 1;
        self.maxAttempts = 3;
        self.userInfo = [NSMutableDictionary dictionary];
        self.file = nil;
        self.requestMethod = requestMethod;
        self.successCallback = successCallback;
        self.failCallback = failCallback;
        
        finished = NO;
    }
    return self;
}

- (void)setValue:(NSObject *)value forKey:(NSString *)key {
    [self.map setObject:value forKey:key];
}

- (void)sendMessageToServer {
    
    NSMutableString *urlString = [NSMutableString stringWithString:self.url];
    
    NSData *postData = [@"" dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    if ([self.map count] > 0 || (self.requestMethod == Post && self.rawPost)) {
        if (self.requestMethod == Get) {
            [urlString appendString:@"?"];
            for (NSString *key in [self.map allKeys]) {
                [urlString appendString:[NSString stringWithFormat:@"%@=%@&", key, [self.map objectForKey:key]]];
            }
        } else {
            NSString *post;
            if (self.rawPost) {
                post = self.rawPost;
            } else {
                SBJSON *parser = [[SBJSON alloc] init];
                post = [parser stringWithObject:self.map];
            }
            postData = [post dataUsingEncoding:NSUTF8StringEncoding];
        }
    }
    
    NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    self.theRequest = [NSMutableURLRequest requestWithURL:url];
    
    NSString *postLength = [NSString stringWithFormat:@"%d", [postData length]];
    
    if (self.requestMethod == Post) {
        [self.theRequest setHTTPMethod:@"POST"];
        [self.theRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [self.theRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    } else if (self.requestMethod == Put) {
        [self.theRequest setHTTPMethod:@"PUT"];
        [self.theRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [self.theRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    } else {
        [self.theRequest setHTTPMethod:@"GET"];
    }
    
    [self.theRequest setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [self.theRequest setHTTPBody:postData];
    [self.theRequest setTimeoutInterval:API_TIMEOUT];
    
    if (self.httpAuthUsername) {
        NSString *authStr = [NSString stringWithFormat:@"%@:%@", self.httpAuthUsername, self.httpAuthPassword];
        NSData *authData = [authStr dataUsingEncoding:NSASCIIStringEncoding];
        NSString *authDataStr = [GTMBase64 stringByEncodingData:authData];
        NSString *authValue = [NSString stringWithFormat:@"Basic %@", authDataStr];
        [self.theRequest setValue:authValue forHTTPHeaderField:@"Authorization"];
    }
    
    while (self.attempt <= self.maxAttempts) {
        if(![self initConnection]) {
            if (self.attempt == self.maxAttempts) {
                //self.failReason = CONNECTION;
                if (self.failCallback) {
                    self.failCallback(self);
                }
            } else {
                self.attempt++;
            }
        } else {
            break;
        }
    }
    
    //IMPORTANT - this keeps the thread alive for the delegate to be called
    while(!finished) {
        [[NSRunLoop currentRunLoop] runMode:NSDefaultRunLoopMode beforeDate:[NSDate distantFuture]];
    }
}

- (BOOL)initConnection {
    
    NSURLConnection *theConnection = [[NSURLConnection alloc] initWithRequest:self.theRequest delegate:self startImmediately:YES];
    
    if (theConnection) {
        self.webData = [NSMutableData data];
        
        //[theConnection setDelegateQueue:self.queue];
        [theConnection start];
    
        return YES;
    } else {
        return NO;
    }
}

-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	[self.webData setLength:0];
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    self.responseCode = [httpResponse statusCode];
}

-(void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	[self.webData appendData:data];
}

-(void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    if (self.attempt == self.maxAttempts) {
        //self.failReason = CONNECTION;
        if (self.failCallback) {
            self.failCallback(self);
        }
    } else {
        self.attempt++;
        [self initConnection];
    }
    
    finished = YES;
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection {
    if (self.isBinaryData) {
        [self processBinary];
    } else {
        [self processNonBinary];
    }
    
    finished = YES;
}

- (void)processBinary {
    
    NSString *value = [GTMBase64 stringByEncodingData:self.webData];
    self.result = value;
    self.successCallback(self);
}

- (void)processNonBinary {
    
    NSString *value = [[NSString alloc] initWithBytes:[self.webData mutableBytes] length:[self.webData length] encoding:NSUTF8StringEncoding];
    self.result = value;
    self.successCallback(self);
    
    /*
    SBJSON *parser = [[SBJSON alloc] init];
    self.result = [parser objectWithString:value];
    
    NSDictionary *first = [[self resultsArray] objectAtIndex:0];
    if ([first objectForKey:@"status"] != nil) {
        self.failReason = SERVERMSG;
        self.serverErrorMessage = [first objectForKey:@"message"];
        if (self.failCallback) {
            self.failCallback(self);
        }
    } else if (!(self.responseCode >= 200 && self.responseCode <= 299)) {
        self.failReason = SERVERERROR;
        self.serverErrorMessage = @"Server Error";
        if (self.failCallback) {
            self.failCallback(self);
        }
    } else {
        if (self.successCallback) {
            self.successCallback(self);
        }
    }*/
}

- (NSDictionary *)resultsDictionary {
    if ([self.result isKindOfClass:[NSDictionary class]]) {
        return self.result;
    } else {
        return nil;
    }
}

- (NSArray *)resultsArray {
    if ([self.result isKindOfClass:[NSArray class]]) {
        return self.result;
    } else {
        return nil;
    }
}

@end
