export interface ProviderStatistics {
  readonly totalByProvider: Record<'FREE' | 'PREMIUM', number>;
  readonly byCompanyAndProvider: Record<string, Record<'FREE' | 'PREMIUM', number>>;
}

export interface OutcomeStatistics {
  readonly totalByOutcome: Record<'FOUND' | 'NO_RESULTS' | 'THIRD_PARTY_DOWN', number>;
  readonly byCompanyAndOutcome: Record<string, Record<'FOUND' | 'NO_RESULTS' | 'THIRD_PARTY_DOWN', number>>;
}

export interface WeeklyCount {
  readonly weekStart: string;
  readonly count: number;
}

export interface VolumeStatistics {
  readonly dailyTotal: Record<string, number>;
  readonly dailyByCompany: Record<string, Record<string, number>>;
  readonly weeklyTotal: WeeklyCount[];
  readonly weeklyByCompany: Record<string, WeeklyCount[]>;
}

export interface UserVolumeStatistics {
  readonly daily: Record<string, number>;
  readonly weekly: WeeklyCount[];
}