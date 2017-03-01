
#Cancerous Cells detection using PET Images and 18FDG radioactive compound 
## Authors : Cyriac Azefack and [QuantIF lab](http://www.litislab.fr/equipe/quantif/)


As part of a PhD thesis, this software has been developped to help on the identification of cancerous cells using *18FDG* 
compound and PET images.

After the injection of the radiactive compound into the patient, we follow the evolution of the radiactive compound throw the body to 
detect which cells are very needy in term of energy (besides the vital organs).
To follow this evolution differents approaches have been implemented on the software : 
- The golden standard [Patlak plot] (https://en.wikipedia.org/wiki/Patlak_plot)
- Hunter method : A static PET image taken 45 - 60mn post injection plus a blood sample at 55mn post injection
- [Barbolosi method] (https://link.springer.com/article/10.1007/s11517-015-1318-3)
