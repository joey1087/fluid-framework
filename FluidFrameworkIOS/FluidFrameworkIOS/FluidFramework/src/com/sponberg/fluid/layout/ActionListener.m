//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/layout/ActionListener.java
//

#include "com/sponberg/fluid/layout/ActionListener.h"

@interface FFTActionListener : NSObject
@end

@implementation FFTActionListener

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "userTappedWithFFTActionListener_EventInfo:", "userTapped", "V", 0x401, NULL },
    { "userChangedValueToWithFFTActionListener_EventInfo:withId:", "userChangedValueTo", "V", 0x401, NULL },
    { "userCancelled", NULL, "V", 0x401, NULL },
    { "userScrolledToBottomWithFFTActionListener_EventInfo:", "userScrolledToBottom", "V", 0x401, NULL },
  };
  static J2ObjcClassInfo _FFTActionListener = { "ActionListener", "com.sponberg.fluid.layout", NULL, 0x201, 4, methods, 0, NULL, 0, NULL};
  return &_FFTActionListener;
}

@end

@implementation FFTActionListener_EventInfo

- (id)init {
  return [super init];
}

- (NSString *)getDataModelKey {
  return self->dataModelKey_;
}

- (NSString *)getDataModelKeyParent {
  return self->dataModelKeyParent_;
}

- (id)getUserInfo {
  return self->userInfo_;
}

- (void)setDataModelKeyWithNSString:(NSString *)dataModelKey {
  self->dataModelKey_ = dataModelKey;
}

- (void)setDataModelKeyParentWithNSString:(NSString *)dataModelKeyParent {
  self->dataModelKeyParent_ = dataModelKeyParent;
}

- (void)setUserInfoWithId:(id)userInfo {
  self->userInfo_ = userInfo;
}

- (BOOL)isEqual:(id)o {
  if (o == self) return YES;
  if (!([o isKindOfClass:[FFTActionListener_EventInfo class]])) return NO;
  FFTActionListener_EventInfo *other = (FFTActionListener_EventInfo *) check_class_cast(o, [FFTActionListener_EventInfo class]);
  if (![((FFTActionListener_EventInfo *) nil_chk(other)) canEqualWithId:(id) check_class_cast(self, [NSObject class])]) return NO;
  id this$dataModelKey = [self getDataModelKey];
  id other$dataModelKey = [other getDataModelKey];
  if (this$dataModelKey == nil ? other$dataModelKey != nil : ![this$dataModelKey isEqual:other$dataModelKey]) return NO;
  id this$dataModelKeyParent = [self getDataModelKeyParent];
  id other$dataModelKeyParent = [other getDataModelKeyParent];
  if (this$dataModelKeyParent == nil ? other$dataModelKeyParent != nil : ![this$dataModelKeyParent isEqual:other$dataModelKeyParent]) return NO;
  id this$userInfo = [self getUserInfo];
  id other$userInfo = [other getUserInfo];
  if (this$userInfo == nil ? other$userInfo != nil : ![this$userInfo isEqual:other$userInfo]) return NO;
  return YES;
}

- (BOOL)canEqualWithId:(id)other {
  return [other isKindOfClass:[FFTActionListener_EventInfo class]];
}

- (NSUInteger)hash {
  int PRIME = 59;
  int result = 1;
  id $dataModelKey = [self getDataModelKey];
  result = result * PRIME + ($dataModelKey == nil ? 0 : ((int) [$dataModelKey hash]));
  id $dataModelKeyParent = [self getDataModelKeyParent];
  result = result * PRIME + ($dataModelKeyParent == nil ? 0 : ((int) [$dataModelKeyParent hash]));
  id $userInfo = [self getUserInfo];
  result = result * PRIME + ($userInfo == nil ? 0 : ((int) [$userInfo hash]));
  return result;
}

- (NSString *)description {
  return [NSString stringWithFormat:@"ActionListener.EventInfo(dataModelKey=%@, dataModelKeyParent=%@, userInfo=%@)", [self getDataModelKey], [self getDataModelKeyParent], [self getUserInfo]];
}

- (void)copyAllFieldsTo:(FFTActionListener_EventInfo *)other {
  [super copyAllFieldsTo:other];
  other->dataModelKey_ = dataModelKey_;
  other->dataModelKeyParent_ = dataModelKeyParent_;
  other->userInfo_ = userInfo_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "init", "EventInfo", NULL, 0x1, NULL },
    { "getDataModelKey", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getDataModelKeyParent", NULL, "Ljava.lang.String;", 0x1, NULL },
    { "getUserInfo", NULL, "Ljava.lang.Object;", 0x1, NULL },
    { "setDataModelKeyWithNSString:", "setDataModelKey", "V", 0x1, NULL },
    { "setDataModelKeyParentWithNSString:", "setDataModelKeyParent", "V", 0x1, NULL },
    { "setUserInfoWithId:", "setUserInfo", "V", 0x1, NULL },
    { "isEqual:", "equals", "Z", 0x1, NULL },
    { "canEqualWithId:", "canEqual", "Z", 0x4, NULL },
    { "hash", "hashCode", "I", 0x1, NULL },
    { "description", "toString", "Ljava.lang.String;", 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "dataModelKey_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
    { "dataModelKeyParent_", NULL, 0x0, "Ljava.lang.String;", NULL,  },
    { "userInfo_", NULL, 0x0, "Ljava.lang.Object;", NULL,  },
  };
  static J2ObjcClassInfo _FFTActionListener_EventInfo = { "EventInfo", "com.sponberg.fluid.layout", "ActionListener", 0x9, 11, methods, 3, fields, 0, NULL};
  return &_FFTActionListener_EventInfo;
}

@end
