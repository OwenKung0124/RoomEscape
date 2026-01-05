/**
 * HasHealth is implemented by any actor that has HP.
 * This  HealthBar work for both Player and Enemy.
 */
public interface HasHealth
{
    //@return current HP
    int getHealth();

    //@return maximum HP
    int getMaxHealth();
}