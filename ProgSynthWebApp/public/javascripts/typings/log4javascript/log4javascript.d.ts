//declare module log4javascript {
    interface Log4Javascript {
        trace(...messages: string[]): void;
        debug(...messages: string[]): void;
        info(...messages: string[]): void;
        warn(...messages: string[]): void;
        error(...messages: string[]): void;
        fatal(...messages: string[]): void;
        addAppender(appender: any): void;
    }

declare function getDefaultLogger(): Log4Javascript;
//}

/*declare module "log4javascript" {
  export = log4javascript;
}*/

declare var log4javascript: Log4Javascript;