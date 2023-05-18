package com.gerp.kasamu.seeder;

import com.gerp.kasamu.model.kasamu.KasamuReviewTopics;
import com.gerp.kasamu.repo.ReviewTopicRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartUpSeeder implements ApplicationListener<ContextRefreshedEvent> {


    private final ReviewTopicRepository reviewTopicRepository;

    public StartUpSeeder(ReviewTopicRepository reviewTopicRepository) {
        this.reviewTopicRepository = reviewTopicRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        seedFirstClassTopics();
        seedSecondClassTopics();
        seedThirdClassTopics();
        seedNonGazztedClassLess();
        seedNonGazzted();
    }

    private void seedNonGazzted() {
       if (!reviewTopicRepository.existsByKasamuClassAndReviewType("NON-GAZETTED","COMMITTEE")){
           KasamuReviewTopics kasamuReviewTopics = new KasamuReviewTopics("वषियवस्तुको ज्ञान र सीप","NON-GAZETTED","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopics);
           KasamuReviewTopics kasamuReviewTopic1 = new KasamuReviewTopics("गोपनीयता राख्न सक्ने सीप","NON-GAZETTED","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic1);
           KasamuReviewTopics kasamuReviewTopic2 = new KasamuReviewTopics("निर्दिेशन अनुसार काम गर्न सक्ने क्षमता","NON-GAZETTED","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic2);
           KasamuReviewTopics kasamuReviewTopic3 = new KasamuReviewTopics("उपस्थति िसमयपालना र अनुशासन","NON-GAZETTED","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic3);
           KasamuReviewTopics kasamuReviewTopic4 = new KasamuReviewTopics("इमान्दारतिा र नैतकिता","NON-GAZETTED","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic4);
       }
    }

    private void seedNonGazztedClassLess() {
       if (!reviewTopicRepository.existsByKasamuClassAndReviewType("CLASS LESS","COMMITTEE")){
           KasamuReviewTopics kasamuReviewTopics = new KasamuReviewTopics("वषियवस्तुको ज्ञान र सीप","CLASS LESS","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopics);
           KasamuReviewTopics kasamuReviewTopic1 = new KasamuReviewTopics("नर्दिेशानुसार काम गर्ने क्षमता","CLASS LESS","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic1);
           KasamuReviewTopics kasamuReviewTopic2 = new KasamuReviewTopics("काममा रुची उत्साह","CLASS LESS","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic2);
           KasamuReviewTopics kasamuReviewTopic3 = new KasamuReviewTopics("आज्ञापालन र अनुशासन","CLASS LESS","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic3);
           KasamuReviewTopics kasamuReviewTopic4 = new KasamuReviewTopics("सजगता र शीघ्रता","CLASS LESS","COMMITTEE");
           reviewTopicRepository.save(kasamuReviewTopic4);
       }
    }

    private void seedThirdClassTopics() {
        if (!reviewTopicRepository.existsByKasamuClassAndReviewType("GAZETTED THIRD","COMMITTEE")) {
            KasamuReviewTopics kasamuReviewTopics = new KasamuReviewTopics("बिषयवस्तुको ज्ञान र सीप", "GAZETTED THIRD", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopics);
            KasamuReviewTopics kasamuReviewTopic1 = new KasamuReviewTopics("विवेकको प्रयोग र निर्णय गर्ने क्षमता", "GAZETTED THIRD", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic1);
            KasamuReviewTopics kasamuReviewTopic2 = new KasamuReviewTopics("कार्यचाप बहन गर्ने क्षमता", "GAZETTED THIRD", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic2);
            KasamuReviewTopics kasamuReviewTopic3 = new KasamuReviewTopics("सृजनशीलता र अग्रसरता", "GAZETTED THIRD", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic3);
            KasamuReviewTopics kasamuReviewTopic4 = new KasamuReviewTopics("पेशागत संवेदनशीलता (गोपनियता र मर्यादित रहने)", "GAZETTED THIRD", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic4);
        }
    }

    private void seedSecondClassTopics() {
        if (!reviewTopicRepository.existsByKasamuClassAndReviewType("GAZETTED SECOND","COMMITTEE")) {
            KasamuReviewTopics kasamuReviewTopics = new KasamuReviewTopics("बिषयवस्तुको ज्ञान र सीप", "GAZETTED SECOND", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopics);
            KasamuReviewTopics kasamuReviewTopic1 = new KasamuReviewTopics("विवेकको प्रयोग र निर्णय गर्ने क्षमता", "GAZETTED SECOND", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic1);
            KasamuReviewTopics kasamuReviewTopic2 = new KasamuReviewTopics("कार्यचाप बहन गर्ने क्षमता", "GAZETTED SECOND", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic2);
            KasamuReviewTopics kasamuReviewTopic3 = new KasamuReviewTopics("सृजनशीलता र अग्रसरता", "GAZETTED SECOND", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic3);
            KasamuReviewTopics kasamuReviewTopic4 = new KasamuReviewTopics("स्रोत साधनको प्रभावकारी उपयोग", "GAZETTED SECOND", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic4);
        }
    }

    private void seedFirstClassTopics() {
        if (!reviewTopicRepository.existsByKasamuClassAndReviewType("GAZETTED FIRST","COMMITTEE")) {
            KasamuReviewTopics kasamuReviewTopics = new KasamuReviewTopics("नीति विश्लेषण गर्ने क्षमता", "GAZETTED FIRST", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopics);
            KasamuReviewTopics kasamuReviewTopic1 = new KasamuReviewTopics("छलपफ्ल तथा वार्ता गर्ने क्षमता", "GAZETTED FIRST", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic1);
            KasamuReviewTopics kasamuReviewTopic2 = new KasamuReviewTopics("विवेकको प्रयोग, निर्णय गर्ने क्षमता र मूल्यांकन", "GAZETTED FIRST", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic2);
            KasamuReviewTopics kasamuReviewTopic3 = new KasamuReviewTopics("नेतृत्व र संगठनात्मक क्षमता", "GAZETTED FIRST", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic3);
            KasamuReviewTopics kasamuReviewTopic4 = new KasamuReviewTopics("पेशागत संवेदनशीलता (इमान्दारिता, गोपनियता आदि)", "GAZETTED FIRST", "COMMITTEE");
            reviewTopicRepository.save(kasamuReviewTopic4);
        }

    }


}
