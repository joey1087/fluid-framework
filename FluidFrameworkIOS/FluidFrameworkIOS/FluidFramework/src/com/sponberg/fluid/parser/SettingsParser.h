//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/parser/SettingsParser.java
//

#ifndef _FFTSettingsParser_H_
#define _FFTSettingsParser_H_

@class FFTFluidApp;
@class FFTKVLReader;
@class IOSObjectArray;
@protocol FFTKeyValueList;

#import "JreEmulation.h"
#include "com/sponberg/fluid/ApplicationInitializer.h"

@interface FFTSettingsParser : NSObject < FFTApplicationInitializer > {
}

- (void)initialize__WithFFTFluidApp:(FFTFluidApp *)app OBJC_METHOD_FAMILY_NONE;

- (void)parseModeSettingsWithFFTFluidApp:(FFTFluidApp *)app
                        withFFTKVLReader:(FFTKVLReader *)reader
                            withNSString:(NSString *)mode;

- (void)setDefaultsWithFFTFluidApp:(FFTFluidApp *)app;

- (void)setColorsWithFFTFluidApp:(FFTFluidApp *)app
             withFFTKeyValueList:(id<FFTKeyValueList>)kvl;

- (void)setSizesWithFFTFluidApp:(FFTFluidApp *)app
            withFFTKeyValueList:(id<FFTKeyValueList>)kvl;

- (IOSObjectArray *)getSupportedPlatforms;

- (id)init;

@end

__attribute__((always_inline)) inline void FFTSettingsParser_init() {}

typedef FFTSettingsParser ComSponbergFluidParserSettingsParser;

#endif // _FFTSettingsParser_H_
