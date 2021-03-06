//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/HttpServiceWrapper.java
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/eclipsesource/json/JsonObject.h"
#include "com/sponberg/fluid/HttpService.h"
#include "com/sponberg/fluid/HttpServiceCallback.h"
#include "com/sponberg/fluid/HttpServiceWrapper.h"
#include "com/sponberg/fluid/util/Logger.h"
#include "com/sponberg/fluid/util/PrettyPrint.h"
#include "java/lang/IllegalArgumentException.h"
#include "java/util/HashMap.h"
#include "java/util/Map.h"
#include "java/util/Set.h"

@implementation FFTHttpServiceWrapper

- (id)initWithFFTHttpService:(id<FFTHttpService>)httpService {
  if (self = [super init]) {
    mapMode_ = FFTHttpServiceWrapper_MapModeEnum_get_Jsonify();
    self->httpService_ = httpService;
  }
  return self;
}

- (void)getWithNSString:(NSString *)URL
    withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
  [FFTLogger debugWithId:self withNSString:@"Http Get {} {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ URL, [FFTPrettyPrint toStringWithJavaUtilMap:parameters] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
  if (parameters != nil) {
    if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Jsonify()) {
      parameters = [FFTHttpServiceWrapper jsonifyMapsWithJavaUtilMap:parameters];
    }
    else if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Bracketify()) {
      parameters = [FFTHttpServiceWrapper bracketifyMapsWithJavaUtilMap:parameters];
    }
  }
  [((id<FFTHttpService>) nil_chk(httpService_)) getWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)getBinaryWithNSString:(NSString *)URL
          withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
   withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
  [FFTLogger debugWithId:self withNSString:@"Http Get Binary {} {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ URL, [FFTPrettyPrint toStringWithJavaUtilMap:parameters] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
  if (parameters != nil) {
    if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Jsonify()) {
      parameters = [FFTHttpServiceWrapper jsonifyMapsWithJavaUtilMap:parameters];
    }
    else if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Bracketify()) {
      parameters = [FFTHttpServiceWrapper bracketifyMapsWithJavaUtilMap:parameters];
    }
  }
  [((id<FFTHttpService>) nil_chk(httpService_)) getBinaryWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)postWithNSString:(NSString *)URL
     withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
  [FFTLogger debugWithId:self withNSString:@"Http Post {} {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ URL, [FFTPrettyPrint toStringWithJavaUtilMap:parameters] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
  if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Jsonify()) {
    parameters = [FFTHttpServiceWrapper jsonifyMapsWithJavaUtilMap:parameters];
  }
  else if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Bracketify()) {
    parameters = [FFTHttpServiceWrapper bracketifyMapsWithJavaUtilMap:parameters];
  }
  [((id<FFTHttpService>) nil_chk(httpService_)) postWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)postRawWithNSString:(NSString *)URL
               withNSString:(NSString *)rawPost
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
 withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
  [FFTLogger debugWithId:self withNSString:@"Http Post {} {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ URL, rawPost } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
  [((id<FFTHttpService>) nil_chk(httpService_)) postRawWithNSString:URL withNSString:rawPost withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

- (void)putWithNSString:(NSString *)URL
    withJavaUtilHashMap:(JavaUtilHashMap *)parameters
withFFTHttpService_HttpAuthorization:(FFTHttpService_HttpAuthorization *)auth
withFFTHttpServiceCallback:(id<FFTHttpServiceCallback>)callback {
  [FFTLogger debugWithId:self withNSString:@"Http Put {} {}" withNSObjectArray:[IOSObjectArray arrayWithObjects:(id[]){ URL, [FFTPrettyPrint toStringWithJavaUtilMap:parameters] } count:2 type:[IOSClass classWithClass:[NSObject class]]]];
  if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Jsonify()) {
    parameters = [FFTHttpServiceWrapper jsonifyMapsWithJavaUtilMap:parameters];
  }
  else if (mapMode_ == FFTHttpServiceWrapper_MapModeEnum_get_Bracketify()) {
    parameters = [FFTHttpServiceWrapper bracketifyMapsWithJavaUtilMap:parameters];
  }
  [((id<FFTHttpService>) nil_chk(httpService_)) putWithNSString:URL withJavaUtilHashMap:parameters withFFTHttpService_HttpAuthorization:auth withFFTHttpServiceCallback:callback];
}

+ (JavaUtilHashMap *)jsonifyMapsWithJavaUtilMap:(id<JavaUtilMap>)parameters {
  JavaUtilHashMap *map = [[JavaUtilHashMap alloc] init];
  for (id<JavaUtilMap_Entry> __strong entry_ in nil_chk([((id<JavaUtilMap>) nil_chk(parameters)) entrySet])) {
    id value = [((id<JavaUtilMap_Entry>) nil_chk(entry_)) getValue];
    if ([value conformsToProtocol: @protocol(JavaUtilMap)]) {
      (void) [map putWithId:[entry_ getKey] withId:[FFTHttpServiceWrapper jsonifyMapsHelperWithJavaUtilMap:(id<JavaUtilMap>) check_protocol_cast(value, @protocol(JavaUtilMap))]];
    }
    else {
      (void) [map putWithId:[entry_ getKey] withId:value];
    }
  }
  return map;
}

+ (NSString *)jsonifyMapsHelperWithJavaUtilMap:(id<JavaUtilMap>)parameters {
  FFTJsonObject *json = [[FFTJsonObject alloc] init];
  for (id<JavaUtilMap_Entry> __strong entry_ in nil_chk([((id<JavaUtilMap>) nil_chk(parameters)) entrySet])) {
    if ([[((id<JavaUtilMap_Entry>) nil_chk(entry_)) getValue] conformsToProtocol: @protocol(JavaUtilMap)]) {
      (void) [json addWithNSString:[entry_ getKey] withNSString:[FFTHttpServiceWrapper jsonifyMapsHelperWithJavaUtilMap:(id<JavaUtilMap>) check_protocol_cast([entry_ getValue], @protocol(JavaUtilMap))]];
    }
    else {
      (void) [json addWithNSString:[entry_ getKey] withNSString:[nil_chk([entry_ getValue]) description]];
    }
  }
  return [json description];
}

+ (JavaUtilHashMap *)bracketifyMapsWithJavaUtilMap:(id<JavaUtilMap>)parameters {
  JavaUtilHashMap *map = [[JavaUtilHashMap alloc] init];
  for (id<JavaUtilMap_Entry> __strong entry_ in nil_chk([((id<JavaUtilMap>) nil_chk(parameters)) entrySet])) {
    id value = [((id<JavaUtilMap_Entry>) nil_chk(entry_)) getValue];
    if ([value conformsToProtocol: @protocol(JavaUtilMap)]) {
      [FFTHttpServiceWrapper bracketifyMapsHelperWithJavaUtilHashMap:map withNSString:[entry_ getKey] withJavaUtilMap:(id<JavaUtilMap>) check_protocol_cast(value, @protocol(JavaUtilMap))];
    }
    else {
      (void) [map putWithId:[entry_ getKey] withId:value];
    }
  }
  return map;
}

+ (void)bracketifyMapsHelperWithJavaUtilHashMap:(JavaUtilHashMap *)map
                                   withNSString:(NSString *)prefix
                                withJavaUtilMap:(id<JavaUtilMap>)parameters {
  for (id<JavaUtilMap_Entry> __strong entry_ in nil_chk([((id<JavaUtilMap>) nil_chk(parameters)) entrySet])) {
    id value = [((id<JavaUtilMap_Entry>) nil_chk(entry_)) getValue];
    if ([value conformsToProtocol: @protocol(JavaUtilMap)]) {
      [FFTHttpServiceWrapper bracketifyMapsHelperWithJavaUtilHashMap:map withNSString:[NSString stringWithFormat:@"%@[%@]", prefix, [entry_ getKey]] withJavaUtilMap:(id<JavaUtilMap>) check_protocol_cast(value, @protocol(JavaUtilMap))];
    }
    else {
      (void) [((JavaUtilHashMap *) nil_chk(map)) putWithId:[NSString stringWithFormat:@"%@[%@]", prefix, [entry_ getKey]] withId:value];
    }
  }
}

- (id<FFTHttpService>)getHttpService {
  return self->httpService_;
}

- (FFTHttpServiceWrapper_MapModeEnum *)getMapMode {
  return self->mapMode_;
}

- (void)setMapModeWithFFTHttpServiceWrapper_MapModeEnum:(FFTHttpServiceWrapper_MapModeEnum *)mapMode {
  self->mapMode_ = mapMode;
}

- (BOOL)isEqual:(id)o {
  if (o == self) return YES;
  if (!([o isKindOfClass:[FFTHttpServiceWrapper class]])) return NO;
  FFTHttpServiceWrapper *other = (FFTHttpServiceWrapper *) check_class_cast(o, [FFTHttpServiceWrapper class]);
  if (![((FFTHttpServiceWrapper *) nil_chk(other)) canEqualWithId:(id) check_class_cast(self, [NSObject class])]) return NO;
  id this$httpService = [self getHttpService];
  id other$httpService = [other getHttpService];
  if (this$httpService == nil ? other$httpService != nil : ![this$httpService isEqual:other$httpService]) return NO;
  id this$mapMode = [self getMapMode];
  id other$mapMode = [other getMapMode];
  if (this$mapMode == nil ? other$mapMode != nil : ![this$mapMode isEqual:other$mapMode]) return NO;
  return YES;
}

- (BOOL)canEqualWithId:(id)other {
  return [other isKindOfClass:[FFTHttpServiceWrapper class]];
}

- (NSUInteger)hash {
  int PRIME = 59;
  int result = 1;
  id $httpService = [self getHttpService];
  result = result * PRIME + ($httpService == nil ? 0 : ((int) [$httpService hash]));
  id $mapMode = [self getMapMode];
  result = result * PRIME + ($mapMode == nil ? 0 : ((int) [$mapMode hash]));
  return result;
}

- (NSString *)description {
  return [NSString stringWithFormat:@"HttpServiceWrapper(httpService=%@, mapMode=%@)", [self getHttpService], [self getMapMode]];
}

- (void)copyAllFieldsTo:(FFTHttpServiceWrapper *)other {
  [super copyAllFieldsTo:other];
  other->httpService_ = httpService_;
  other->mapMode_ = mapMode_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "initWithFFTHttpService:", "HttpServiceWrapper", NULL, 0x1, NULL },
    { "getWithNSString:withJavaUtilHashMap:withFFTHttpService_HttpAuthorization:withFFTHttpServiceCallback:", "get", "V", 0x1, NULL },
    { "getBinaryWithNSString:withJavaUtilHashMap:withFFTHttpService_HttpAuthorization:withFFTHttpServiceCallback:", "getBinary", "V", 0x1, NULL },
    { "postWithNSString:withJavaUtilHashMap:withFFTHttpService_HttpAuthorization:withFFTHttpServiceCallback:", "post", "V", 0x1, NULL },
    { "postRawWithNSString:withNSString:withFFTHttpService_HttpAuthorization:withFFTHttpServiceCallback:", "postRaw", "V", 0x1, NULL },
    { "putWithNSString:withJavaUtilHashMap:withFFTHttpService_HttpAuthorization:withFFTHttpServiceCallback:", "put", "V", 0x1, NULL },
    { "jsonifyMapsWithJavaUtilMap:", "jsonifyMaps", "Ljava.util.HashMap;", 0xc, NULL },
    { "jsonifyMapsHelperWithJavaUtilMap:", "jsonifyMapsHelper", "Ljava.lang.String;", 0xc, NULL },
    { "bracketifyMapsWithJavaUtilMap:", "bracketifyMaps", "Ljava.util.HashMap;", 0xc, NULL },
    { "bracketifyMapsHelperWithJavaUtilHashMap:withNSString:withJavaUtilMap:", "bracketifyMapsHelper", "V", 0xc, NULL },
    { "getHttpService", NULL, "Lcom.sponberg.fluid.HttpService;", 0x1, NULL },
    { "getMapMode", NULL, "Lcom.sponberg.fluid.HttpServiceWrapper$MapMode;", 0x1, NULL },
    { "setMapModeWithFFTHttpServiceWrapper_MapModeEnum:", "setMapMode", "V", 0x1, NULL },
    { "isEqual:", "equals", "Z", 0x1, NULL },
    { "canEqualWithId:", "canEqual", "Z", 0x4, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "httpService_", NULL, 0x10, "Lcom.sponberg.fluid.HttpService;", NULL,  },
    { "mapMode_", NULL, 0x2, "Lcom.sponberg.fluid.HttpServiceWrapper$MapMode;", NULL,  },
  };
  static J2ObjcClassInfo _FFTHttpServiceWrapper = { "HttpServiceWrapper", "com.sponberg.fluid", NULL, 0x1, 17, methods, 2, fields, 0, NULL};
  return &_FFTHttpServiceWrapper;
}

@end

BOOL FFTHttpServiceWrapper_MapModeEnum_initialized = NO;

FFTHttpServiceWrapper_MapModeEnum *FFTHttpServiceWrapper_MapModeEnum_values[2];

@implementation FFTHttpServiceWrapper_MapModeEnum

- (id)copyWithZone:(NSZone *)zone {
  return self;
}

- (id)initWithNSString:(NSString *)__name withInt:(int)__ordinal {
  return [super initWithNSString:__name withInt:__ordinal];
}

+ (void)initialize {
  if (self == [FFTHttpServiceWrapper_MapModeEnum class]) {
    FFTHttpServiceWrapper_MapModeEnum_Jsonify = [[FFTHttpServiceWrapper_MapModeEnum alloc] initWithNSString:@"Jsonify" withInt:0];
    FFTHttpServiceWrapper_MapModeEnum_Bracketify = [[FFTHttpServiceWrapper_MapModeEnum alloc] initWithNSString:@"Bracketify" withInt:1];
    FFTHttpServiceWrapper_MapModeEnum_initialized = YES;
  }
}

+ (IOSObjectArray *)values {
  return [IOSObjectArray arrayWithObjects:FFTHttpServiceWrapper_MapModeEnum_values count:2 type:[IOSClass classWithClass:[FFTHttpServiceWrapper_MapModeEnum class]]];
}

+ (FFTHttpServiceWrapper_MapModeEnum *)valueOfWithNSString:(NSString *)name {
  for (int i = 0; i < 2; i++) {
    FFTHttpServiceWrapper_MapModeEnum *e = FFTHttpServiceWrapper_MapModeEnum_values[i];
    if ([name isEqual:[e name]]) {
      return e;
    }
  }
  @throw [[JavaLangIllegalArgumentException alloc] initWithNSString:name];
  return nil;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "Jsonify", "Jsonify", 0x4019, "Lcom.sponberg.fluid.HttpServiceWrapper$MapMode;", &FFTHttpServiceWrapper_MapModeEnum_Jsonify,  },
    { "Bracketify", "Bracketify", 0x4019, "Lcom.sponberg.fluid.HttpServiceWrapper$MapMode;", &FFTHttpServiceWrapper_MapModeEnum_Bracketify,  },
  };
  static const char *superclass_type_args[] = {"Lcom.sponberg.fluid.HttpServiceWrapper$MapMode;"};
  static J2ObjcClassInfo _FFTHttpServiceWrapper_MapModeEnum = { "MapMode", "com.sponberg.fluid", "HttpServiceWrapper", 0x4019, 1, methods, 2, fields, 1, superclass_type_args};
  return &_FFTHttpServiceWrapper_MapModeEnum;
}

@end
