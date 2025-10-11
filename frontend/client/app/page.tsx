import { Header } from "@/components/header"
import { HeroSection } from "@/components/hero-section"
import { NowShowingSection } from "@/components/now-showing-section"
import { ComingSoonSection } from "@/components/coming-soon-section"
import { MembershipSection } from "@/components/membership-section"
import { FacilitiesSection } from "@/components/facilities-section"
import { TestimonialsSection } from "@/components/testimonials-section"
import { FAQSection } from "@/components/faq-section"
import { ContactSection } from "@/components/contact-section"
import { Footer } from "@/components/footer"

export default function Home() {
  return (
    <main className="min-h-screen bg-background">
      <HeroSection />
      <NowShowingSection />
      <ComingSoonSection />
      <MembershipSection />
      <FacilitiesSection />
      <TestimonialsSection />
      <FAQSection />
      <ContactSection />
    </main>
  )
}
