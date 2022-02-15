export interface NotificareAsset {
  readonly title: string;
  readonly description?: string;
  readonly key?: string;
  readonly url?: string;
  readonly button?: NotificareAssetButton;
  readonly metaData?: NotificareAssetMetaData;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  readonly extra: Record<string, any>;
}

export interface NotificareAssetButton {
  readonly label?: string;
  readonly action?: string;
}

export interface NotificareAssetMetaData {
  readonly originalFileName: string;
  readonly contentType: string;
  readonly contentLength: number;
}
