function radioReflectSim(normVecs)
close all
% imgdata = readData();
% normVecs = processData(imgdata);

rayAngle = 70; %With respect to the vertical
power = 100;

numRays = 1000;

%First is theta, second is phi, 3rd is power, 4th is bounce, 5th is xdist
inputRays = [rayAngle * ones(numRays, 1), zeros(numRays, 1), power * ones(numRays, 1)];

rays = [inputRays, ones(numRays, 1), zeros(numRays, 1)];

salinity = 35;
temp = 15;
eps = 71;
eps0 = 8.854e-12;
height = 100; %Miles if it matters
xMax = 500; %Miles
n = sqrt(eps * eps0); %Assume that relative permability is 1

f = .5;%Void Fraction
epsBub = .25 * (sqrt(44100*f^2 - 59220*f +20449) - 210*f + 141);

output = bounce(rays);
output2 = output;
numb = 1;
while(size(output2, 1) > numRays/10)
    output = bounce(output);
    
    output2 = trimTooWide(output);
%     if(mod(l,10) == 0)
%         figure('name', ['Angles after ' , num2str(l) , ' bounce'])
%         hist(output(:,1))
%         title(['Angles after ' , num2str(l) , ' bounce'])
%     end
    numb = numb + 1;
    disp(size(output2, 1))
end

% figure('name', ['Power after ' , num2str(numb),' bounce')
% hist(output(:,2))
% title('Power after 100 bounce')

figure('name', ['Bounces after ' , num2str(numb),' max bounces'])
hist(output(:,4))
title(['Bounces after ' , num2str(numb),' bounces'])


    function outputRays = bounce(input)
%         outputRays = ionBounce(input);
        outputRays = calmWaterBounce(input);
    end

% Simulates reflection off of calm water
    function outputRays = calmWaterBounce(input)
       %If power > 10 dB
       
       angles = input(:, 1);
       anglesPhi = input(:, 2);
       powers = input(:, 3);
       numBounce = input(:, 4);
       xDist = input(:, 5);
       
%        anglesOut = someFuckingVectorMath(angles);
       
       for(k = 1:size(input, 1))
%            alpha = sqrt(1 - ((1/n) * sin(angles(k)))^2)/cos(angles(k));
%            beta = n;
           if((powers(k, 1) > 10) && (abs(xDist(k, 1)) < xMax))
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
               
               %Calculates reflection coefficient (currently assuming
               %efield is parallel with incident plane
               r = (n*cosd(outAngle) - cosd(angles(k, 1))) / (cosd(angles(k, 1) + ...
                   n*cosd(outAngle))); %Reflection coefficient
               
               %applies attenuation
               powers(k, 1) = powers(k, 1) * r^2;
               %Update all angles
               
               %Updates bounce count
               numBounce(k, 1) = numBounce(k, 1) + 1;
               
               %Updates HorizontalDist
               trueDist = height/sind(angles(k,1));
               xDist(k, 1) = xDist(k, 1) + sind(anglesPhi(k,1))*trueDist;
               
               %Updates angles
               angles(k, 1) = outAngle;   
               if(~isreal(outAngle))
                    disp(outAngle)
               end
               anglesPhi(k, 1) = outAnglePhi;
               if(~isreal(outAnglePhi))
                    disp(outAnglePhi)
                    
               end
               
           end
       end
%        disp(angles)
       outputRays = [angles, anglesPhi, powers, numBounce, xDist];
    end
    
    function weak = isSignalWeak(input)
        powers = input(:, 2);
        weak = 1;
        k = 1;
        while(weak == 1 && k <= size(input,1))
            weak = powers(k) <= 10;
            k = k + 1;
        end
    end
    
    function data = readData()
        angleNum = 5;
        data = {};
        while(angleNum <= 90)
            picNum = 1;
            while(picNum <= 3)
                data{angleNum/5, picNum} = imread(['mcm/s' ...
                    , num2str(angleNum) , '_' , num2str(picNum) , '.png']);
                picNum = picNum + 1;
            end
            angleNum = angleNum + 5;
        end
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
        
        newAngle(1,2) = acos(dot(normalizedXZOutput, [0, 0, -1])) * 180 / pi;
%         if(newAngle > 90)
%             fprintf('%f\t%f\t%f\t%f\n', proj1, norm(1,1), norm(1,2),norm(1,3))
%             fprintf('%f\t%f\t%f\t%f\n', proj2, perpNorm(1,1), perpNorm(1,2), perpNorm(1,3))%, outPutVector)
%         end
    end

    function outputRays = ionBounce(input)
       angles = input(:, 1);
       anglesPhi = input(:, 2);
       powers = input(:, 3);
       numBounce = input(:, 4);
       xDist = input(:, 5);
       
       %Updates HorizontalDist
       for(k = 1:size(input,1))
           trueDist = height/sind(angles(k, 1));
           xDist(k, 1) = xDist(k, 1) + sind(anglesPhi(k,1))*trueDist;
           outputRays = [angles, anglesPhi, powers, numBounce, xDist];
       end
    end

    function outputRays = trimTooWide(input)
        xDist = input(:, 5);
        
        k = 1;
        s = size(xDist, 1);
        while(k <= s)
            if(xDist(k, 1) > xMax)
                input = [input(1:k-1, :); input(k+1:size(input,1), :)];
                s = s - 1;
            end
            k = k + 1;
        end
        
        outputRays = input;
    end
    
end