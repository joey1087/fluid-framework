//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/util/CastUtil.java
//

#ifndef _FFTCastUtil_H_
#define _FFTCastUtil_H_

@class IOSClass;
@class JavaUtilHashMap;

#import "JreEmulation.h"

@interface FFTCastUtil : NSObject {
}

+ (BOOL)isArrayOfPrimitivesWithIOSClass:(IOSClass *)c;

+ (id)getNullOrPrimitiveDefaultWithIOSClass:(IOSClass *)to;

+ (id)castWithId:(id)o
    withIOSClass:(IOSClass *)to;

- (id)init;

@end

FOUNDATION_EXPORT BOOL FFTCastUtil_initialized;
J2OBJC_STATIC_INIT(FFTCastUtil)

FOUNDATION_EXPORT JavaUtilHashMap *FFTCastUtil_primitiveDefaults_;
J2OBJC_STATIC_FIELD_GETTER(FFTCastUtil, primitiveDefaults_, JavaUtilHashMap *)
J2OBJC_STATIC_FIELD_SETTER(FFTCastUtil, primitiveDefaults_, JavaUtilHashMap *)

typedef FFTCastUtil ComSponbergFluidUtilCastUtil;

#endif // _FFTCastUtil_H_
