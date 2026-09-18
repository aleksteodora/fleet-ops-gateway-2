export type SearchStatus = 'FOUND' | 'NO_RESULTS' | 'THIRD_PARTY_DOWN';

export interface VehicleData {
  readonly make: string;
  readonly model: string;
  readonly modelYear: number;
  readonly fuelType: string;
  readonly engine: string;
  readonly vehicleStatus: string;
}

export interface VehicleSearchResult {
  readonly status: SearchStatus;
  readonly vehicleData: VehicleData | null;
}