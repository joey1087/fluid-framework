//
//  FFTNSUrlProtocol.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 8/04/2014.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFNSUrlProtocol.h"
#import "ViewBehaviorWebView.h"

@implementation FFNSUrlProtocol

+ (BOOL)canInitWithRequest:(NSURLRequest*)theRequest {
    
    NSString *scheme = [theRequest.URL scheme];
    if ([scheme isEqualToString:@"fluid"]) {
        NSString *command = theRequest.URL.host;
        if ([command isEqualToString:@"load"]) {
            return YES;
        }
    }
    return NO;
}

+ (NSURLRequest*)canonicalRequestForRequest:(NSURLRequest*)theRequest {
    return theRequest;
}

- (void)startLoading {
    
    NSString *name = [[self.request.URL pathComponents] objectAtIndex:1];
    
    NSString *dataAsString = [FFTViewBehaviorWebView getFileWithNSString:name];
    
    NSData* data = [dataAsString dataUsingEncoding:NSUTF8StringEncoding];
    
    NSString *mimeType;
    if ([self.request.URL.pathExtension isEqualToString:@"js"]) {
        mimeType = @"text/javascript";
    } else if ([self.request.URL.pathExtension isEqualToString:@"css"]) {
        mimeType = @"text/css";
    } else {
        return [super startLoading];
    }
    
    NSURLResponse *response = [[NSURLResponse alloc] initWithURL:self.request.URL MIMEType:mimeType expectedContentLength:[data length] textEncodingName:@"UTF-8"];
    
    [[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageAllowed];
    [[self client] URLProtocol:self didLoadData:data];
    [[self client] URLProtocolDidFinishLoading:self];
}

- (void)stopLoading {
}

@end
