#import "AppDelegate+NotificarePlugin.h"

/// Plugin name in config.xml
static NSString *const PLUGIN_NAME = @"Notificare";

@implementation AppDelegate (NotificarePlugin)

- (BOOL)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> * _Nullable))restorationHandler {
    CDVPlugin *plugin = [[self viewController] getCommandInstance:PLUGIN_NAME];
    if (plugin == nil) {
        return NO;
    }

    SEL selector = NSSelectorFromString(@"handleUserActivity:");
    if (![plugin respondsToSelector:selector]) {
        return NO;
    }

    IMP imp = [plugin methodForSelector:selector];
    BOOL (*func)(id, SEL, NSUserActivity *) = (void *)imp;
    BOOL result = func(plugin, selector, userActivity);

    return result;
}

@end
