/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.timer.schedule;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.ScheduleExpression;

/**
 * Represents the value of a second constructed out of a {@link ScheduleExpression#getSecond()}
 *
 * <p>
 *  A {@link Second} can hold only {@link Integer} as its value. The only exception to this being the wildcard (*)
 *  value. The various ways in which a 
 *  {@link Second} value can be represented are:
 *  <ul>
 *      <li>Wildcard. For example, second = "*"</li>
 *      <li>Range. For example, second = "0-34"</li>
 *      <li>List. For example, second = "15, 20, 59"</li>
 *      <li>Single value. For example, second = "12"</li>
 *      <li>Increment. For example, second = "* &#47; 5"</li>
 *  </ul>
 * </p>
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class Second extends IntegerBasedExpression
{

   /**
    * The maximum allowed value for a second
    */
   public static final Integer MAX_SECOND = 59;

   /**
    * Minimum allowed value for a second
    */
   public static final Integer MIN_SECOND = 0;

   /**
    * A sorted set of valid values for seconds, created out
    * of a {@link ScheduleExpression#getSecond()} 
    */
   private SortedSet<Integer> seconds = new TreeSet<Integer>();

   /**
    * The type of the expression, from which this {@link Second} was 
    * constructed.
    * 
    * @see ScheduleExpressionType
    */
   private ScheduleExpressionType expressionType;

   /**
    * Creates a {@link Second} by parsing the passed {@link String} <code>value</code>
    * <p>
    *   Valid values are of type {@link ScheduleExpressionType#WILDCARD}, {@link ScheduleExpressionType#RANGE},
    *   {@link ScheduleExpressionType#LIST} {@link ScheduleExpressionType#INCREMENT} or 
    *   {@link ScheduleExpressionType#SINGLE_VALUE}
    * </p>
    * @param value The value to be parsed
    * 
    * @throws IllegalArgumentException If the passed <code>value</code> is neither a {@link ScheduleExpressionType#WILDCARD}, 
    *                               {@link ScheduleExpressionType#RANGE}, {@link ScheduleExpressionType#LIST}, 
    *                               {@link ScheduleExpressionType#INCREMENT} nor {@link ScheduleExpressionType#SINGLE_VALUE}.
    * 
    */
   public Second(String value)
   {
      // check the type of value
      this.expressionType = ScheduleExpressionTypeUtil.getType(value);

      Set<Integer> secs = null;
      switch (this.expressionType)
      {
         case RANGE :
            RangeValue range = new RangeValue(value);
            // process the range value and get integer values
            // out of it
            secs = this.processRangeValue(range);
            // add to our sorted set
            this.seconds.addAll(secs);
            break;

         case LIST :
            ListValue list = new ListValue(value);
            // process the list value and get integer values
            // out of it
            secs = this.processListValue(list);
            // add to our sorted set
            this.seconds.addAll(secs);
            break;

         case INCREMENT :
            IncrementValue incrValue = new IncrementValue(value);
            // process the increment value and get integer values
            // out of it
            secs = this.processIncrement(incrValue);
            // add to our sorted set
            this.seconds.addAll(secs);
            break;

         case SINGLE_VALUE :
            SingleValue singleValue = new SingleValue(value);
            // process the single value and get the integer value
            // out of it
            Integer sec = this.processSingleValue(singleValue);
            // add it to our sorted set
            this.seconds.add(sec);
            break;

         case WILDCARD :
            // a wildcard is equivalent to "all possible" values, so 
            // do nothing
            break;

         default :
            throw new IllegalArgumentException("Invalid value for second: " + value);
      }
   }

   public Calendar getNextSecond(Calendar current)
   {
      Calendar next = new GregorianCalendar(current.getTimeZone());
      next.setTime(current.getTime());

      Integer currentSecond = current.get(Calendar.SECOND);
      if (this.expressionType == ScheduleExpressionType.WILDCARD)
      {
         return current;
      }
      Integer nextSecond = seconds.first();
      for (Integer second : seconds)
      {
         if (currentSecond.equals(second))
         {
            nextSecond = currentSecond;
            break;
         }
         if (second.intValue() > currentSecond.intValue())
         {
            nextSecond = second;
            break;
         }
      }
      if (nextSecond < currentSecond)
      {
         // advance to next minute
         next.add(Calendar.MINUTE, 1);
      }
      next.set(Calendar.SECOND, nextSecond);

      return next;
   }

   /**
    * Returns the maximum allowed value for a {@link Second}
    * 
    * @see Second#MAX_SECOND
    */
   @Override
   protected Integer getMaxValue()
   {
      return MAX_SECOND;
   }

   /**
    * Returns the minimum allowed value for a {@link Second}
    * 
    * @see Second#MIN_SECOND
    */
   @Override
   protected Integer getMinValue()
   {
      return MIN_SECOND;
   }

}
