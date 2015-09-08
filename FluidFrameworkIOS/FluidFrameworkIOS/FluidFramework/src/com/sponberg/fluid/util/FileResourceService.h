//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/util/FileResourceService.java
//

#ifndef _FFTFileResourceService_H_
#define _FFTFileResourceService_H_

@class IOSByteArray;

#import "JreEmulation.h"
#include "com/sponberg/fluid/ResourceService.h"

@interface FFTFileResourceService : NSObject < FFTResourceService > {
 @public
  NSString *workingDir_;
}

- (NSString *)getResourceAsStringWithNSString:(NSString *)dir
                                 withNSString:(NSString *)name;

- (IOSByteArray *)getResourceAsBytesWithNSString:(NSString *)dir
                                    withNSString:(NSString *)name;

- (NSString *)getWorkingDir;

- (void)setWorkingDirWithNSString:(NSString *)workingDir;

- (void)saveResourceWithNSString:(NSString *)dir
                    withNSString:(NSString *)name
                   withByteArray:(IOSByteArray *)bytes
                     withBoolean:(BOOL)excludeFromBackup;

- (BOOL)resourceExistsWithNSString:(NSString *)dir
                      withNSString:(NSString *)name;

- (void)saveImageWithNSString:(NSString *)dir
                 withNSString:(NSString *)name
                       withId:(id)object
                  withBoolean:(BOOL)excludeFromBackup;

- (id)getImageWithNSString:(NSString *)dir
              withNSString:(NSString *)name;

- (id)init;

- (void)copyAllFieldsTo:(FFTFileResourceService *)other;

@end

__attribute__((always_inline)) inline void FFTFileResourceService_init() {}

J2OBJC_FIELD_SETTER(FFTFileResourceService, workingDir_, NSString *)

FOUNDATION_EXPORT NSString *FFTFileResourceService_kRoot_;
J2OBJC_STATIC_FIELD_GETTER(FFTFileResourceService, kRoot_, NSString *)

typedef FFTFileResourceService ComSponbergFluidUtilFileResourceService;

#endif // _FFTFileResourceService_H_
