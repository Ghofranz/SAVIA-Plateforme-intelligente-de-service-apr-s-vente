import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AiAnalysisResponse } from '../models/ai-analysis.model';

@Injectable({
providedIn: 'root'
})
export class AiAnalysisApiService {
private readonly apiUrl = 'http://localhost:8080/ai-service/api/ai-analyses';

constructor(private readonly http: HttpClient) {}

  getAnalysisBySavCaseId(savCaseId: number): Observable<AiAnalysisResponse> {
    return this.http.get<AiAnalysisResponse>(`${this.apiUrl}/sav-case/${savCaseId}`);
  }
}
