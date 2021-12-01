interface C1<T> {}
interface C2<T, U>{}

export class KipHelperData {

  private static instance: KipHelperData;

  readonly defaultInitialFormValues: Map<any, any> = new Map<any, any>();

  constructor() {
    this.buildDefaultInitialFormValues();
  }

  public static getInstance(): KipHelperData {
    if (!this.instance) {
      this.instance = new KipHelperData();
    }
    return this.instance;
  }

  buildDefaultInitialFormValues(): void {
    this.defaultInitialFormValues.set(String, '');
    this.defaultInitialFormValues.set(Boolean, false);
  }

  static getDefaultInitialFormValue<T>(type: T): any {
    return KipHelperData.getInstance().defaultInitialFormValues.get(type);
  }

  static instantiateTypes1<T>(type: (new () => C1<T>)): C1<T> {
    return new type();
  }

  static instantiateTypes2<T, U>(type: (new () => C2<T, U>)): C2<T, U> {
    return new type();
  }

  static getDefaultInitialObjectValueTypes1<T>(container: any): C1<T> {
    return KipHelperData.instantiateTypes1<T>(container.constructor);
  }

  static getDefaultInitialObjectValueTypes2<T, U>(container: any): C2<T, U> {
    return KipHelperData.instantiateTypes2<T, U>(container.constructor);
  }

  static getEmptyContainer(containerRef: any) {
    switch (containerRef.constructor.name) {
      case 'Set':
        const setAsArr = Array.from(containerRef);
        return new Set<typeof setAsArr[number]>();
      case 'Array':
        return new Array<typeof containerRef[number]>();
    }
    throw new TypeError('unsupported container type');
  }

  static resetContainer(container: any): void {
    switch(container.constructor.name) {
      case 'Set':
      case 'Map':
        container.clear();
        break;
      case 'Array':
        container.splice(0, container.length);
        break;
      default:
        throw new TypeError('unsupported container type: ' + container.constructor.name);
    }
  }

  static addKeysWithValue<T, U>(map: Map<T, U>, value: U, ... keys: T[]) {
    keys.map(key => {
      map.set(key, value);
    });
  }

}
