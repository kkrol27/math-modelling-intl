function p = radioReflectSim(normVecs)
% close all
% imgdata = readData();
% normVecs = processData(imgdata);

rayAngle = 80; %With respect to the vertical

power = 100;
numRays = 10000;
amplitude = sqrt(power/numRays * 4*pi*1e-7 * 3e8);

%First is theta, second is phi, 3rd is amplitude, 4th is bounce, 5th is xdist
inputRays = [rayAngle * ones(numRays, 1), zeros(numRays, 1), amplitude * ones(numRays, 1)];
%6th is zdist
rays = [inputRays, zeros(numRays, 1), zeros(numRays, 1), zeros(numRays, 1)];

frequency = 15e6;
waveLen = 3e8/frequency;

salinity = 35;
Oceantemp = 15;
eps = 71; %Ocean
% eps = 7; %Ground
eps0 = 8.854e-12;
height = 289682; %Meters if it matters
xMax = 10000; %Meters
n = sqrt(eps);%sqrt(eps * eps0); %Assume that relative permability is 1

fo = .5;%Void Fraction
epsBub = .25 * (sqrt(44100*fo^2 - 59220*fo +20449) - 210*fo + 141);

output = rays;
output2 = output;
numb = 1;
statePhi = zeros(1, numRays);
stateDists = zeros(numRays*4, 1);%zeros(1, numRays);
totalPower = power;
statePower = zeros(numRays*4, 1);

setMaxBounce = 4;

while(numb <= setMaxBounce)%totalPower > .1 * power)%size(output2, 1) > numRays/10)
    output = bounce(output);
    output2 = output;%trimTooWide(output);
    totalPower = evalPower(output2);
    
    %distsTrimmed = output2(:,5);
    distanceTravelled = output(:, 6);
    for(index = 1:numRays)
        stateDists((numb-1)*numRays + index, 1) = distanceTravelled(index, 1);
        statePower((numb-1)*numRays + index, 1) = calcRayPower(output(index, 3));
    end
    
    
    horizDist = output(:,5);
    phiAngs = output(:,2);
%     statePhi(numb, :) = phiAngs';
%     stateDists(numb, :) = output(:,5)';
%     if(mod(l,10) == 0)
%         figure('name', ['Angles after ' , num2str(l) , ' bounce'])
%         hist(output(:,1))
%         title(['Angles after ' , num2str(l) , ' bounce'])
%     end
    numb = numb + 1;
%     disp(size(output2, 1))
end
% output = bounce(output);
% p = output;%evalPower(output);%output;
% figure('name', ['Power after ' , num2str(numb),' bounce')
% hist(output(:,2))
% title('Power after 100 bounce')

numb = numb - 1;
% p = numb;
% figure('name', ['Number of bounces before 10 dB attenuation.'])
% hist(output(:,4))
% title(['Number of Bounces Before 10 dB Attenuation'])
% % xticks([2 3])
% xlabel('Number of Bounces')
% ylabel('Number of Rays')

figure('name', ['Power vs Distance'])
plot(stateDists, statePower, '.')
title(['Ray Power vs Distance'])
% xticks([2 3])
xlabel('Distance (m)')
ylabel('Ray Power (Watts)')

% figure('name', ['Dists ' , num2str(numb),' max bounces'])
% plot(stateDists, '.')
% title(['Dists after ' , num2str(numb),' bounces'])
% 
% figure('name', ['Phi ' , num2str(numb),' max bounces'])
% plot(statePhi, '.')
% title(['Phi ' , num2str(numb),' bounces'])

    function outputRays = bounce(input)
        outputRays = ionBounce(input);
        outputRays = calmWaterBounce(input);
    end

% Simulates reflection off of calm water
    function outputRays = calmWaterBounce(input)
       %If power > 10 dB
       
       angles = input(:, 1);
       anglesPhi = input(:, 2);
       amplitudes = input(:, 3);
       numBounce = input(:, 4);
       xDist = input(:, 5);
       zDist = input(:, 6);
       
%        anglesOut = someFuckingVectorMath(angles);
       
       for(k = 1:size(input, 1))
%            alpha = sqrt(1 - ((1/n) * sin(angles(k)))^2)/cos(angles(k));
%            beta = n;
%            if((abs(xDist(k, 1)) < xMax))%(powers(k, 1) > 10) && 
               %Calculates reflected angles
               outAngleArray = reflectVector([angles(k, 1), anglesPhi(k, 1)]);
               outAngle = outAngleArray(1,1);
               outAnglePhi = outAngleArray(1,2);
               while(outAngle > 90)
                   outAngle = 180 - outAngle;
                   outAngleArray = reflectVector([outAngle, outAnglePhi]);
                   outAngle = outAngleArray(1,1);
                   outAnglePhi = outAngleArray(1,2);
               end
               
               if(rand < .50)
                   nUse = sqrt(epsBub);
               else
                   nUse = n;
               end
               
%                nUse = n;
               
               %Calculates reflection coefficient (currently assuming
               %efield is parallel with incident plane
               num = nUse*cosd(outAngle) - cosd(angles(k, 1));
               denom = cosd(angles(k, 1)) + nUse*cosd(outAngle);
               r = num/denom; %Reflection coefficient
               
               %applies attenuation
               amplitudes(k, 1) = amplitudes(k, 1) * abs(r);
               %Update all angles
               
               %Updates bounce count
               numBounce(k, 1) = numBounce(k, 1) + 1;
               
               %Updates HorizontalDist
               trueDist = height/sind(angles(k,1));
%                if(sind(anglesPhi(k,1))*trueDist < 0)
%                    disp(anglesPhi(k,1))
%                end
               xDist(k, 1) = xDist(k, 1) + sind(anglesPhi(k,1))*trueDist;
               zDist(k, 1) = zDist(k, 1) + trueDist * ...
                   cosd(anglesPhi(k,1))*cosd(angles(k,1));
               
               %Updates angles
               angles(k, 1) = outAngle;   
               anglesPhi(k, 1) = outAnglePhi;
               
%            end
       end
%        disp(angles)
       outputRays = [angles, anglesPhi, amplitudes, numBounce, xDist, zDist];
    end

    function newAngle = reflectVector(inputAngles)
        %radInput = pi /180 * inputAngles;
        thetaAngle = inputAngles(1,1);
        
        phiAngle = inputAngles(1,2);
        inputVector = [sind(phiAngle), -cosd(thetaAngle), -sind(thetaAngle)*cosd(phiAngle)];
        inputNorm = sqrt(inputVector(1,1)^2 + inputVector(1,2)^2 + inputVector(1,3)^2);
        inputVector = inputVector/inputNorm;
        angleBin = ceil((90 - thetaAngle)/5);
        numPoints = size(normVecs{angleBin}, 1);
        
        norm = normVecs{angleBin, 1}(floor(rand * (numPoints - 1) + 1),:);
%         disp(norm)
        proj1 = -1 * dot(norm, inputVector);
        perpNorm = cross(norm, cross(inputVector, norm));
        proj2 = dot(inputVector, perpNorm);
        
        outputVector = proj1 * norm + proj2 * perpNorm;
        
%         disp(dot(outPutVector, [0, 1, 0]))

        newAngle(1,1) = acos(dot(outputVector, [0, 1, 0])) * 180 / pi;
        
        normalizerOutput = sqrt(outputVector(1,1)^2 + outputVector(1,3)^2);
        normalizedXZOutput = [outputVector(1,1)/normalizerOutput, 0, ...
            outputVector(1,3)/normalizerOutput];
        
        newAngle(1,2) = asin(dot(normalizedXZOutput, [1, 0, 0])) * 180 / pi;
        
%         if(newAngle > 90)
%             fprintf('%f\t%f\t%f\t%f\n', proj1, norm(1,1), norm(1,2),norm(1,3))
%             fprintf('%f\t%f\t%f\t%f\n', proj2, perpNorm(1,1), perpNorm(1,2), perpNorm(1,3))%, outPutVector)
%         end
    end

    function outputRays = ionBounce(input)
       angles = input(:, 1);
       anglesPhi = input(:, 2);
       amplitudes = input(:, 3);
       numBounce = input(:, 4);
       xDist = input(:, 5);
       zDist = input(:, 6);
%        ptot = 0; %Total air pressure
%        rp = ptot/1010;
%        f = 15e6;
%        t = 0%Air temp
%        rp = 288/(273 + t)
%        chi1 = psi(rp, rt, .0717, -1.8132, 0.0156, -1.6515);
%        chi2 = psi(rp, rt, .5146, -4.6368, -.192, -5.7416);
%        chi3 = psi(rp, rt, .3414, -6.5851, .2130, -8.5854);
%        atten = (((7.2 * rt^2.8)/(f^2 + .34*rp^2*rt^1.6) +...
%            .62*chi3/((54 - f)^(1.16 * chi1)+.83*chi2)))*f^2 * rp^2*10^-3;
       
       
       
       %Updates HorizontalDist
       for(k = 1:size(input,1))
           trueDist = height/sind(angles(k, 1));
           xDist(k, 1) = xDist(k, 1) + sind(anglesPhi(k,1))*trueDist;
           zDist(k, 1) = zDist(k, 1) + trueDist * ...
                   cosd(anglesPhi(k,1))*cosd(angles(k,1));
       end
       outputRays = [angles, anglesPhi, amplitudes, numBounce, xDist, zDist];
    end

    function outputRays = trimTooWide(input)
        xDist = input(:, 5);
        
        k = 1;
        s = size(xDist, 1);
        while(k <= s)
            test = abs(input(k,5));
            if(abs(input(k, 5)) > xMax)
                lasdf = 1;
                if(k == size(input,1))
                    input = input(1:k-1, :);
                elseif (k == 1)
                    input = [input(k+1:size(input,1), :)];
                else
                    test2 = input(:, 5);
                    input = [input(1:k-1, :); input(k+1:size(input,1), :)];
                    test2 = input(:, 5);
                end
                s = s - 1;
            else
                k = k + 1;
            end
        end
        
        outputRays = input;
    end

    function out = psi(rp, rt, a, b, c, d)
        out = rp^a * rt^b * exp(c*(1-rp) + d*(1-rt));
    end
    

    function pwr = evalPower(input)
       angles = input(:, 1);
       anglesPhi = input(:, 2);
       amplitudes = input(:, 3);
       numBounce = input(:, 4);
       xDist = input(:, 5);
       zDist = input(:, 6);
       pwr = 0;
       for(k = 1:size(input, 1))
%            wavePower = amplitudes(k,1)^2 * (1/xDist(k,1))^2;
           
           wavePower = amplitudes(k,1)^2 / (4*pi*1e-7 * 3e8);
           adjustZDist = abs(zDist(k,1));
           ratiodB = 20*log10(4*pi*adjustZDist/(waveLen));
           pwr = pwr + wavePower; %* 10^(-ratiodB/10);
       end
    end

    function pwr = calcRayPower(inputAmp)
        pwr = zeros(size(inputAmp, 1));
        for(k = 1: size(inputAmp, 1))
            pwr(k, 1) = inputAmp(k,1)^2 / (4*pi*1e-7 * 3e8);
        end
    end
end