function s = wavespectrum(w, h)
    
    c = (8.1e-3) * 9.81^2 ./ w.^5;
    e = exp( -0.032 * 9.81^2 ./ (h^2 .* w.^4) );
    s = c .* e;

end