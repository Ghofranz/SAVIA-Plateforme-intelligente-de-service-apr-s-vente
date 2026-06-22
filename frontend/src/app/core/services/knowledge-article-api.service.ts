import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { KnowledgeArticleResponse } from '../models/knowledge-article.model';

@Injectable({
providedIn: 'root'
})
export class KnowledgeArticleApiService {
private readonly apiUrl = 'http://localhost:8080/ai-service/api/knowledge-articles';

constructor(private readonly http: HttpClient) {}

  getAllArticles(): Observable<KnowledgeArticleResponse[]> {
    return this.http.get<KnowledgeArticleResponse[]>(this.apiUrl);
  }

  getArticleById(id: number): Observable<KnowledgeArticleResponse> {
    return this.http.get<KnowledgeArticleResponse>(`${this.apiUrl}/${id}`);
  }

  validateArticle(id: number): Observable<KnowledgeArticleResponse> {
    return this.http.put<KnowledgeArticleResponse>(`${this.apiUrl}/${id}/validate`, {});
  }

  archiveArticle(id: number): Observable<KnowledgeArticleResponse> {
    return this.http.put<KnowledgeArticleResponse>(`${this.apiUrl}/${id}/archive`, {});
  }
}
