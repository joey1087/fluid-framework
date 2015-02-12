//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/fluid/WebviewEventsManager.java
//

#include "IOSObjectArray.h"
#include "com/sponberg/fluid/WebviewEventsManager.h"
#include "com/sponberg/fluid/layout/WebviewActionListener.h"
#include "java/util/ArrayList.h"
#include "java/util/HashMap.h"

@implementation FFTWebviewEventsManager

- (void)addEventListenerWithFFTWebviewActionListener:(id<FFTWebviewActionListener>)listener
                                   withNSStringArray:(IOSObjectArray *)keyPath {
  [self addEventListenerHelperWithFFTWebviewEventsManager_WebviewEventListenerGroup:rootEventListenerGroup_ withNSStringArray:keyPath withInt:0 withFFTWebviewActionListener:listener];
}

- (void)actionPerformedWithNSString:(NSString *)keyPath
                       withNSString:(NSString *)userInfo {
  IOSObjectArray *tokens = [self getTokensFromPathWithNSString:keyPath];
  FFTWebviewEventsManager_WebviewEventListenerGroup *group = [self getEventListenerGroupWithFFTWebviewEventsManager_WebviewEventListenerGroup:rootEventListenerGroup_ withNSStringArray:tokens withInt:0];
  if (group != nil) {
    for (id<FFTWebviewActionListener> __strong listener in nil_chk([group getListener])) {
      [((id<FFTWebviewActionListener>) nil_chk(listener)) actionPerformedWithNSString:userInfo];
    }
  }
}

- (BOOL)isListeningForTapAtWithNSString:(NSString *)viewPath {
  IOSObjectArray *tokens = [self getTokensFromPathWithNSString:viewPath];
  FFTWebviewEventsManager_WebviewEventListenerGroup *group = [self getEventListenerGroupWithFFTWebviewEventsManager_WebviewEventListenerGroup:rootEventListenerGroup_ withNSStringArray:tokens withInt:0];
  return group != nil && [((JavaUtilArrayList *) nil_chk([group getListener])) size] > 0;
}

- (FFTWebviewEventsManager_WebviewEventListenerGroup *)getEventListenerGroupWithFFTWebviewEventsManager_WebviewEventListenerGroup:(FFTWebviewEventsManager_WebviewEventListenerGroup *)group
                                                                                                                withNSStringArray:(IOSObjectArray *)tokens
                                                                                                                          withInt:(int)tokenIndex {
  NSString *key = IOSObjectArray_Get(nil_chk(tokens), tokenIndex);
  FFTWebviewEventsManager_WebviewEventListenerGroup *nextGroup = [((FFTWebviewEventsManager_WebviewEventListenerGroup *) nil_chk(group)) getWithNSString:key];
  if (nextGroup == nil) {
    return nil;
  }
  if (tokenIndex == (int) [tokens count] - 1) {
    return nextGroup;
  }
  else {
    return [self getEventListenerGroupWithFFTWebviewEventsManager_WebviewEventListenerGroup:nextGroup withNSStringArray:tokens withInt:tokenIndex + 1];
  }
}

- (void)addEventListenerHelperWithFFTWebviewEventsManager_WebviewEventListenerGroup:(FFTWebviewEventsManager_WebviewEventListenerGroup *)group
                                                                  withNSStringArray:(IOSObjectArray *)tokens
                                                                            withInt:(int)tokenIndex
                                                       withFFTWebviewActionListener:(id<FFTWebviewActionListener>)listener {
  NSString *key = IOSObjectArray_Get(nil_chk(tokens), tokenIndex);
  FFTWebviewEventsManager_WebviewEventListenerGroup *nextGroup = [((FFTWebviewEventsManager_WebviewEventListenerGroup *) nil_chk(group)) getWithNSString:key];
  if (nextGroup == nil) {
    nextGroup = [[FFTWebviewEventsManager_WebviewEventListenerGroup alloc] init];
    [group putWithNSString:key withFFTWebviewEventsManager_WebviewEventListenerGroup:nextGroup];
  }
  if (tokenIndex == (int) [tokens count] - 1) {
    [((FFTWebviewEventsManager_WebviewEventListenerGroup *) nil_chk(nextGroup)) addListenerWithFFTWebviewActionListener:listener];
  }
  else {
    [self addEventListenerHelperWithFFTWebviewEventsManager_WebviewEventListenerGroup:nextGroup withNSStringArray:tokens withInt:tokenIndex + 1 withFFTWebviewActionListener:listener];
  }
}

- (IOSObjectArray *)getTokensFromPathWithNSString:(NSString *)keyPath {
  IOSObjectArray *tokens = [((NSString *) nil_chk(keyPath)) split:@"\\."];
  for (int i = 0; i < (int) [((IOSObjectArray *) nil_chk(tokens)) count]; i++) {
    int index = [((NSString *) IOSObjectArray_Get(tokens, i)) indexOfString:@"|"];
    if (index != -1) {
      (void) IOSObjectArray_Set(tokens, i, [((NSString *) IOSObjectArray_Get(tokens, i)) substring:0 endIndex:index]);
    }
  }
  return tokens;
}

- (id)init {
  if (self = [super init]) {
    rootEventListenerGroup_ = [[FFTWebviewEventsManager_WebviewEventListenerGroup alloc] init];
  }
  return self;
}

- (void)copyAllFieldsTo:(FFTWebviewEventsManager *)other {
  [super copyAllFieldsTo:other];
  other->rootEventListenerGroup_ = rootEventListenerGroup_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "addEventListenerWithFFTWebviewActionListener:withNSStringArray:", "addEventListener", "V", 0x81, NULL },
    { "actionPerformedWithNSString:withNSString:", "actionPerformed", "V", 0x1, NULL },
    { "isListeningForTapAtWithNSString:", "isListeningForTapAt", "Z", 0x1, NULL },
    { "getEventListenerGroupWithFFTWebviewEventsManager_WebviewEventListenerGroup:withNSStringArray:withInt:", "getEventListenerGroup", "Lcom.sponberg.fluid.WebviewEventsManager$WebviewEventListenerGroup;", 0x1, NULL },
    { "addEventListenerHelperWithFFTWebviewEventsManager_WebviewEventListenerGroup:withNSStringArray:withInt:withFFTWebviewActionListener:", "addEventListenerHelper", "V", 0x4, NULL },
    { "getTokensFromPathWithNSString:", "getTokensFromPath", "[Ljava.lang.String;", 0x2, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "rootEventListenerGroup_", NULL, 0x0, "Lcom.sponberg.fluid.WebviewEventsManager$WebviewEventListenerGroup;", NULL,  },
  };
  static J2ObjcClassInfo _FFTWebviewEventsManager = { "WebviewEventsManager", "com.sponberg.fluid", NULL, 0x1, 7, methods, 1, fields, 0, NULL};
  return &_FFTWebviewEventsManager;
}

@end

@implementation FFTWebviewEventsManager_WebviewEventListenerGroup

- (void)addListenerWithFFTWebviewActionListener:(id<FFTWebviewActionListener>)listener {
  [((JavaUtilArrayList *) nil_chk(listeners_)) addWithId:listener];
}

- (JavaUtilArrayList *)getListener {
  return listeners_;
}

- (FFTWebviewEventsManager_WebviewEventListenerGroup *)getWithNSString:(NSString *)key {
  return [((JavaUtilHashMap *) nil_chk(listenerGroups_)) getWithId:key];
}

- (void)putWithNSString:(NSString *)key
withFFTWebviewEventsManager_WebviewEventListenerGroup:(FFTWebviewEventsManager_WebviewEventListenerGroup *)group {
  (void) [((JavaUtilHashMap *) nil_chk(listenerGroups_)) putWithId:key withId:group];
}

- (id)init {
  if (self = [super init]) {
    listenerGroups_ = [[JavaUtilHashMap alloc] init];
    listeners_ = [[JavaUtilArrayList alloc] init];
  }
  return self;
}

- (void)copyAllFieldsTo:(FFTWebviewEventsManager_WebviewEventListenerGroup *)other {
  [super copyAllFieldsTo:other];
  other->listenerGroups_ = listenerGroups_;
  other->listeners_ = listeners_;
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "addListenerWithFFTWebviewActionListener:", "addListener", "V", 0x1, NULL },
    { "getListener", NULL, "Ljava.util.ArrayList;", 0x1, NULL },
    { "getWithNSString:", "get", "Lcom.sponberg.fluid.WebviewEventsManager$WebviewEventListenerGroup;", 0x0, NULL },
    { "putWithNSString:withFFTWebviewEventsManager_WebviewEventListenerGroup:", "put", "V", 0x0, NULL },
    { "init", NULL, NULL, 0x0, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "listenerGroups_", NULL, 0x0, "Ljava.util.HashMap;", NULL,  },
    { "listeners_", NULL, 0x0, "Ljava.util.ArrayList;", NULL,  },
  };
  static J2ObjcClassInfo _FFTWebviewEventsManager_WebviewEventListenerGroup = { "WebviewEventListenerGroup", "com.sponberg.fluid", "WebviewEventsManager", 0x8, 5, methods, 2, fields, 0, NULL};
  return &_FFTWebviewEventsManager_WebviewEventListenerGroup;
}

@end
