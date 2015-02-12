//
//  FFWebView.m
//  FluidFrameworkIOS
//
//  Created by Hans Sponberg on 23/03/14.
//  Copyright (c) 2014 Hans Sponberg. All rights reserved.
//

#import "FFWebView.h"
#import "FFFluidAppDelegate.h"
#import "FluidApp.h"
#import "DataModelManager.h"
#import "HtmlUtil.h"
#import "FFDataNotificationService.h"
#import "FFNSUrlProtocol.h"
#import "GlobalState.h"
#include "com/eclipsesource/json/JsonObject.h"
#include "UIService.h"
#include "IOSClass.h"
#include "com/sponberg/fluid/util/Logger.h"
#include "WebviewEventsManager.h"

@implementation FFWebView

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setDelegate:self];
        [self setBackgroundColor:[UIColor clearColor]];
        [NSURLProtocol registerClass:[FFNSUrlProtocol class]];
    }
    return self;
}

- (void)makeLessWebby {

    ((UIScrollView*)[self scrollView]).bounces = NO;
    
    //self.backgroundColor = [UIColor whiteColor];
}

- (void)flashScrollIndicators {
    // Only flashes if there is scrollable content
    [[self scrollView] flashScrollIndicators];
}

- (void)viewDidLoad {
    [NSURLProtocol registerClass:[FFNSUrlProtocol class]];
}

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request navigationType:(UIWebViewNavigationType)navigationType {
    NSString *scheme = [request.URL scheme];
    if ([scheme isEqualToString:@"file"]) {
        return YES;
    } else {
        if ([scheme isEqualToString:@"fluid"]) {
            NSString *command = request.URL.host;
            if ([command isEqualToString:@"data"]) {
                
                // If success, then return a json object, where each dataKey will be paired with its data
                // If not success, then return an error message string
                
                NSString *dataKeyString = [[request.URL pathComponents] objectAtIndex:1];
                NSArray *dataKeys = [dataKeyString componentsSeparatedByString:@","];
                
                NSArray *callbackTokens = [[request.URL query] componentsSeparatedByString:@"="];
                
                BOOL success = YES;
                
                FFTJsonObject *json = [[FFTJsonObject alloc] init];
                
                NSString *data;
                if (![callbackTokens[0] isEqualToString:@"callback"]) {
                    data = [NSString stringWithFormat:@"Invalid query %@", request.URL.query];
                    success = NO;
                } else {
                    for (NSString *dataKey in dataKeys) {
                        
                        NSString *safeDataKey = [dataKey stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceCharacterSet]];
                        
                        data = [[[FFTGlobalState fluidApp] getDataModelManager] getValueWithNSString:nil withNSString:safeDataKey withNSString:@"{0}" withNSString:nil];
                        if (data == nil) {
                            success = NO;
                            data = [NSString stringWithFormat:@"No data for %@", safeDataKey];
                            break;
                        } else {
                            [json addWithNSString:safeDataKey withNSString:data];
                        }
                    }
                }
                
                id dataToReturn = data;
                
                if (success) {
                    dataToReturn = [NSString stringWithFormat:@"%@", json];
                    
                    dataToReturn = [ComSponbergFluidUtilHtmlUtil escapeSingleQuoteWithNSString:dataToReturn];
                    
                    // dataToReturn may have escapes, but that will trip up javascript. We need to double escape those.
                    dataToReturn = [ComSponbergFluidUtilHtmlUtil escapeBackslashesWithNSString:dataToReturn];
                }
                
                NSString *callbackId = callbackTokens[1];
                
                NSString *jsFunctionCall = [NSString stringWithFormat:@"fluidDataCallback('%@',%d,'%@');", callbackId, success, dataToReturn];
                
                [self stringByEvaluatingJavaScriptFromString:jsFunctionCall];
                
            } else if ([command isEqualToString:@"addDataChangeListener"]) {

                NSString *key = [[request.URL pathComponents] objectAtIndex:1];
                
                NSArray *callbackTokens = [[request.URL query] componentsSeparatedByString:@"="];
                
                BOOL success = YES;
                
                __weak typeof(self) weakSelf = self;
                
                if (![callbackTokens[0] isEqualToString:@"callback"]) {
                    success = NO;
                }
                
                if (success) {
                    NSString *callbackId = callbackTokens[1]; // TODO: hstdbc change observerId to view id
                    id<FFFluidAppDelegate> appDelegate = (id<FFFluidAppDelegate>) [[UIApplication sharedApplication] delegate];
                    observerBlock block = ^(NSString *key, NSArray *subkeys) {
                        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{                                                                                      dispatch_sync(dispatch_get_main_queue(), ^{
                                NSString *jsFunctionCall = [NSString stringWithFormat:@"dataDidChangeFor('%@','%@','');", callbackId, key];
                                [weakSelf stringByEvaluatingJavaScriptFromString:jsFunctionCall];
                            });
                        });
                    };
                    observerBlockDataRemoved blockRemoved = ^(NSString *key) {
                        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{                                                                                      dispatch_sync(dispatch_get_main_queue(), ^{
                                // hstdbc todo
                            });
                        });
                    };
                    
                    [[appDelegate dataNotificationService] addDataChangeObserverFor:nil
                                                                                key:key
                                                                         observerId:callbackId
                                                                  listenForChildren:NO
                                                                              block:block
                                                                   blockDataRemoved:blockRemoved];
                }
            } else if ([command isEqualToString:@"uiService"]) {
                
                NSArray *components = [[request.URL.path substring:1] componentsSeparatedByString:@"/"];
                [self handleUiService:components];
            } else if ([command isEqualToString:@"action"]) {
                
                NSArray *components = [[request.URL.path substring:1] componentsSeparatedByString:@"/"];
                [self handleAction:components];
            } else {
                
                NSArray *components = [[request.URL.path substring:1] componentsSeparatedByString:@"/"];
                [self handleCommand:command components:components];
            }
            
            NSString *jsFunctionCall = [NSString stringWithFormat:@"commandFinished();"];
            [self stringByEvaluatingJavaScriptFromString:jsFunctionCall];

            return NO;
        }
        return NO;
    }
}

- (void)handleUiService:(NSArray *)components {
    
    NSString *method = components[0];
    
    if ([method isEqualToString:@"popLayout"]) {
        
        [[FFTGlobalState_fluidApp__ getUiService] popLayout];
    } else if ([method isEqualToString:@"pushLayout"]) {
        
        [[FFTGlobalState_fluidApp__ getUiService] pushLayoutWithNSString:components[1]];
    }
}

- (void)handleAction:(NSArray *)components {
    
    NSString *viewPathAction = [NSString stringWithFormat:@"%@.%@", self.viewPath, components[0]];
    
    NSString *userInfo = nil;
    if (components.count > 1) {
        userInfo = components[1];
    }
    
    [[[FFTGlobalState fluidApp] getWebviewEventsManager] actionPerformedWithNSString:viewPathAction withNSString:userInfo];
}

- (void)handleCommand:(NSString *)command components:(NSArray *)components {
    
    [FFTLogger infoWithId:self withNSString:@"Command not handled: {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ command } count:1 type:[IOSClass classWithClass:[NSObject class]]]];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView {
    [self makeLessWebby];
    [self performSelector:@selector(flashScrollIndicators) withObject:nil afterDelay:.3];
}

@end
