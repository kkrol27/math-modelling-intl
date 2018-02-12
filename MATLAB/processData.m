%R = x, g = y (vertical), b = z
function data = processData(str)
    input = readData();
    data = {};
    dataCounter = 1;
    i = 1;

    while(i <= size(input,1))
        j = 1;
        vectorArray = [];
        while(j <= size(input, 2))
            colorArray = input{i,j};
%                 disp(colorArray)
            vectorCounter = 1;
            colorRow = 1;
            while(colorRow <= size(colorArray, 1))
               colorCol = 1;

               while(colorCol <= size(colorArray, 2))
                   red = colorArray(colorRow, colorCol, 1);
                   green = colorArray(colorRow, colorCol, 2);
                   blue = colorArray(colorRow, colorCol, 3);

                   if(red ~= 0 || green ~= 0 || blue ~= 0)
                       red = 2 * double(red)/255 - 1;
                       green = 2 * double(green)/255 - 1;
                       blue = 2 * double(blue)/255 - 1;
                       
                       norm = sqrt(red^2 + green^2 + blue^2);
                       
                       red = red/norm;
                       green = green/norm;
                       blue = blue/norm;
                       
                       vectorArray(vectorCounter, :) = [red, green, blue];
                       vectorCounter = vectorCounter + 1;
                   end
                   colorCol = colorCol + 5;
               end
               colorRow = colorRow + 5;
            end

            j = j + 1;
        end
        data{dataCounter, 1} = vectorArray;
        dataCounter = dataCounter + 1;
        i = i + 1; 
    end
    
    function data = readData()
        angleNum = 5;
        data = {};
        while(angleNum <= 90)
            picNum = 1;
            while(picNum <= 3)
                data{angleNum/5, picNum} = imread(['mcm/', str ...
                    , num2str(angleNum) , '_' , num2str(picNum) , '.png']);
                picNum = picNum + 1;
            end
            angleNum = angleNum + 5;
        end
    end
end