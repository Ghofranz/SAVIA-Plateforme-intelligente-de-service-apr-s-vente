import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { KnowledgeArticleApiService } from '../../core/services/knowledge-article-api.service';
import {
KnowledgeArticleResponse,
KnowledgeArticleStatus
} from '../../core/models/knowledge-article.model';

@Component({
selector: 'app-knowledge-base',
standalone: true,
imports: [CommonModule, RouterLink],
templateUrl: './knowledge-base.html',
styleUrl: './knowledge-base.scss'
})
export class KnowledgeBase implements OnInit {
private readonly knowledgeArticleApiService = inject(KnowledgeArticleApiService);

readonly articles = signal<KnowledgeArticleResponse[]>([]);
readonly selectedArticle = signal<KnowledgeArticleResponse | null>(null);
readonly selectedStatus = signal<KnowledgeArticleStatus | 'ALL'>('DRAFT');

readonly loading = signal(false);
readonly actionLoading = signal(false);
readonly errorMessage = signal('');
readonly successMessage = signal('');

readonly filteredArticles = computed(() => {
const status = this.selectedStatus();

if (status === 'ALL') {
      return this.articles();
    }

    return this.articles().filter((article) => article.status === status);
  });

  ngOnInit(): void {
    this.loadArticles();
  }

  loadArticles(): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.knowledgeArticleApiService.getAllArticles().subscribe({
      next: (articles) => {
        this.loading.set(false);
        this.articles.set(this.sortArticles(articles));

        const currentSelected = this.selectedArticle();

        if (currentSelected) {
          const refreshed = articles.find((article) => article.id === currentSelected.id);
          this.selectedArticle.set(refreshed || null);
        }
      },
      error: (error) => {
        this.loading.set(false);
        this.errorMessage.set(error.error?.message || 'Erreur lors du chargement des fiches de connaissance.');
      }
    });
  }

  selectStatus(status: KnowledgeArticleStatus | 'ALL'): void {
    this.selectedStatus.set(status);
    this.selectedArticle.set(null);
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  selectArticle(article: KnowledgeArticleResponse): void {
    this.selectedArticle.set(article);
    this.errorMessage.set('');
    this.successMessage.set('');
  }

  validateArticle(article: KnowledgeArticleResponse): void {
    this.actionLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.knowledgeArticleApiService.validateArticle(article.id).subscribe({
      next: (updatedArticle) => {
        this.actionLoading.set(false);
        this.updateLocalArticle(updatedArticle);
        this.selectedArticle.set(updatedArticle);
        this.successMessage.set('Fiche validée et indexée dans le RAG avec succès.');
      },
      error: (error) => {
        this.actionLoading.set(false);
        this.errorMessage.set(error.error?.message || 'Erreur lors de la validation de la fiche.');
      }
    });
  }

  archiveArticle(article: KnowledgeArticleResponse): void {
    this.actionLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.knowledgeArticleApiService.archiveArticle(article.id).subscribe({
      next: (updatedArticle) => {
        this.actionLoading.set(false);
        this.updateLocalArticle(updatedArticle);
        this.selectedArticle.set(updatedArticle);
        this.successMessage.set('Fiche archivée avec succès.');
      },
      error: (error) => {
        this.actionLoading.set(false);
        this.errorMessage.set(error.error?.message || 'Erreur lors de l’archivage de la fiche.');
      }
    });
  }

  statusLabel(status: KnowledgeArticleStatus): string {
    const labels: Record<KnowledgeArticleStatus, string> = {
      DRAFT: 'À valider',
      VALIDATED: 'Validée',
      ARCHIVED: 'Archivée'
    };

    return labels[status];
  }

  statusClass(status: KnowledgeArticleStatus): string {
    return status.toLowerCase();
  }

  countByStatus(status: KnowledgeArticleStatus | 'ALL'): number {
    if (status === 'ALL') {
      return this.articles().length;
    }

    return this.articles().filter((article) => article.status === status).length;
  }

  private updateLocalArticle(updatedArticle: KnowledgeArticleResponse): void {
    const updatedArticles = this.articles().map((article) =>
      article.id === updatedArticle.id ? updatedArticle : article
    );

    this.articles.set(this.sortArticles(updatedArticles));
  }

  private sortArticles(articles: KnowledgeArticleResponse[]): KnowledgeArticleResponse[] {
    return [...articles].sort((a, b) => {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    });
  }
}
