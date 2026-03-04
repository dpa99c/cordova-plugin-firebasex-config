interface FirebasexConfig {
    fetch(
        cacheExpirationSeconds: number,
        success: () => void,
        error: (err: string) => void
    ): void;
    fetch(
        success: () => void,
        error: (err: string) => void
    ): void;
    activateFetched(
        success: (activated: boolean) => void,
        error: (err: string) => void
    ): void;
    fetchAndActivate(
        success: (activated: boolean) => void,
        error: (err: string) => void
    ): void;
    resetRemoteConfig(
        success: () => void,
        error: (err: string) => void
    ): void;
    getValue(
        key: string,
        success: (value: string) => void,
        error: (err: string) => void
    ): void;
    getAll(
        success: (values: object) => void,
        error: (err: string) => void
    ): void;
    getInfo(
        success: (info: object) => void,
        error: (err: string) => void
    ): void;
    setConfigSettings(
        fetchTimeout: number,
        minimumFetchInterval: number,
        success: () => void,
        error: (err: string) => void
    ): void;
    setDefaults(
        defaults: object,
        success: () => void,
        error: (err: string) => void
    ): void;
}
