//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: src-delomboked/com/sponberg/app/ui/SearchWhereAndroid.java
//

#include "IOSClass.h"
#include "IOSObjectArray.h"
#include "com/sponberg/app/SampleApp.h"
#include "com/sponberg/app/ui/Screen.h"
#include "com/sponberg/app/ui/SearchWhereAndroid.h"
#include "com/sponberg/fluid/FluidApp.h"
#include "com/sponberg/fluid/Platforms.h"
#include "com/sponberg/fluid/layout/ActionListener.h"
#include "com/sponberg/fluid/layout/MenuButtonItem.h"
#include "com/sponberg/fluid/layout/Screen.h"
#include "java/util/List.h"

@implementation FASearchWhereAndroid

- (void)load__WithFFTFluidApp:(FFTFluidApp *)fApp {
  self->app_ = (FASampleApp *) check_class_cast(fApp, [FASampleApp class]);
  FFTMenuButtonItem *item = [[FFTMenuButtonItem alloc] initWithNSString:FFTMenuButtonItem_get_SystemItemSearch_() withNSString:@"Search" withNSString:nil withInt:FFTMenuButtonItem_ActionFlavorSearch];
  [item setPropertyWithNSString:@"queryHint" withNSString:@"2000 - Sydney"];
  [item setPropertyWithNSString:@"textColor" withNSString:@"255,255,255"];
  [item addActionListenerWithFFTActionListener:[[FASearchWhereAndroid_$1 alloc] initWithFASearchWhereAndroid:self]];
  [((id<JavaUtilList>) nil_chk([((FFTScreen *) nil_chk([((FASampleApp *) nil_chk(app_)) getScreenWithNSString:FAScreen_get_SearchWhere_()])) getNavigationMenuItems])) addWithId:item];
  (void) [((FFTFluidApp_AddActionListenerBuilder *) nil_chk([app_ addActionListenerWithNSStringArray:[IOSObjectArray arrayWithObjects:(id[]){ FAScreen_get_SearchWhere_(), FAScreen_ScreenSearchWhere_get_Results_() } count:2 type:[IOSClass classWithClass:[NSString class]]]])) listenerWithFFTActionListener:[[FASearchWhereAndroid_$2 alloc] initWithFASearchWhereAndroid:self]];
}

- (IOSObjectArray *)getSupportedPlatforms {
  return [IOSObjectArray arrayWithObjects:(id[]){ FFTPlatforms_get_Android_() } count:1 type:[IOSClass classWithClass:[NSString class]]];
}

- (id)init {
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "load__WithFFTFluidApp:", "load", "V", 0x1, NULL },
    { "getSupportedPlatforms", NULL, "[Ljava.lang.String;", 0x1, NULL },
    { "init", NULL, NULL, 0x1, NULL },
  };
  static J2ObjcClassInfo _FASearchWhereAndroid = { "SearchWhereAndroid", "com.sponberg.app.ui", NULL, 0x1, 3, methods, 0, NULL, 0, NULL};
  return &_FASearchWhereAndroid;
}

@end

@implementation FASearchWhereAndroid_$1

- (void)userChangedValueToWithFFTActionListener_EventInfo:(FFTActionListener_EventInfo *)info
                                                   withId:(id)value {
  [this$0_ userSearchedForWithId:value];
}

- (id)initWithFASearchWhereAndroid:(FASearchWhereAndroid *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "userChangedValueToWithFFTActionListener_EventInfo:withId:", "userChangedValueTo", "V", 0x1, NULL },
    { "initWithFASearchWhereAndroid:", "init", NULL, 0x0, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "Lcom.sponberg.app.ui.SearchWhereAndroid;", NULL,  },
  };
  static J2ObjcClassInfo _FASearchWhereAndroid_$1 = { "$1", "com.sponberg.app.ui", "SearchWhereAndroid", 0x8000, 2, methods, 1, fields, 0, NULL};
  return &_FASearchWhereAndroid_$1;
}

@end

@implementation FASearchWhereAndroid_$2

- (void)userTappedWithFFTActionListener_EventInfo:(FFTActionListener_EventInfo *)info {
  [this$0_ userTappedSearchResultWithId:[((FFTActionListener_EventInfo *) nil_chk(info)) getUserInfo]];
}

- (id)initWithFASearchWhereAndroid:(FASearchWhereAndroid *)outer$ {
  this$0_ = outer$;
  return [super init];
}

+ (J2ObjcClassInfo *)__metadata {
  static J2ObjcMethodInfo methods[] = {
    { "userTappedWithFFTActionListener_EventInfo:", "userTapped", "V", 0x1, NULL },
    { "initWithFASearchWhereAndroid:", "init", NULL, 0x0, NULL },
  };
  static J2ObjcFieldInfo fields[] = {
    { "this$0_", NULL, 0x1012, "Lcom.sponberg.app.ui.SearchWhereAndroid;", NULL,  },
  };
  static J2ObjcClassInfo _FASearchWhereAndroid_$2 = { "$2", "com.sponberg.app.ui", "SearchWhereAndroid", 0x8000, 2, methods, 1, fields, 0, NULL};
  return &_FASearchWhereAndroid_$2;
}

@end
