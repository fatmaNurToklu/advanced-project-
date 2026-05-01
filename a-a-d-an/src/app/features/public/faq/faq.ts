import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq.html',
  styleUrl: './faq.css',
})
export class FaqComponent {
  openIndex = signal<number | null>(null);

  faqs = [
    { q: 'How do I place an order?', a: 'Browse products, add items to your cart, select a delivery address and click "Place Order".' },
    { q: 'What payment methods are accepted?', a: 'We accept credit/debit cards, bank transfers and digital wallets.' },
    { q: 'How can I track my order?', a: 'Go to "Order History" in your account. Each order shows real-time shipping status.' },
    { q: 'Can I return a product?', a: 'Yes, within 30 days of delivery. Contact support to initiate a return.' },
    { q: 'How do I become a corporate seller?', a: 'Register and select "Corporate" account type. Your store request will be reviewed within 24 hours.' },
    { q: 'Is my payment information secure?', a: 'Yes. All payments are processed with 256-bit SSL encryption. We never store card details.' },
  ];

  toggle(index: number) {
    this.openIndex.set(this.openIndex() === index ? null : index);
  }
}
