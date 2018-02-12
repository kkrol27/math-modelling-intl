function getDistribution(data1, data2)

% powers = zeros(50,1);
% for(n = 1:100)
%     powers(n,1) = radioReflectSim(data);
% end
% close all
% 
% figure('name', ['Power After 1 Bounce on Rough Ground'])
% hist(powers)
% title(['Power After 1 Bounce on Rough Ground'])
% % title('Number of Bounces Before 10 dB Attenuation')
% % xticks([2 3 4])
% xlabel('Power (Watts)')
% ylabel('Number of Trials')

figure('name', 'Angle Distributions after 1 Bounce')
subplot(2,1,1)
output1 = radioReflectSim(data1);
hist(output1(:,1))
title('Smooth Ground Distribution')
xlabel('Angles')
ylabel('Number of Rays')

subplot(2,1,2)
output2 = radioReflectSim(data2);
hist(output2(:,1))
title('Rough Ground Distribution')
xlabel('Angles')
ylabel('Number of Rays')
end